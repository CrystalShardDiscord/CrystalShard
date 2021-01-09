package org.comroid.crystalshard.gateway;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.comroid.api.ContextualProvider;
import org.comroid.api.IntEnum;
import org.comroid.common.info.MessageSupplier;
import org.comroid.crystalshard.*;
import org.comroid.crystalshard.gateway.event.DispatchEventType;
import org.comroid.crystalshard.gateway.event.GatewayEvent;
import org.comroid.crystalshard.gateway.event.generic.*;
import org.comroid.mutatio.pipe.Pipe;
import org.comroid.mutatio.pump.Pump;
import org.comroid.mutatio.ref.FutureReference;
import org.comroid.mutatio.ref.Reference;
import org.comroid.restless.socket.Websocket;
import org.comroid.restless.socket.WebsocketPacket;
import org.comroid.uniform.SerializationAdapter;
import org.comroid.uniform.ValueType;
import org.comroid.uniform.node.UniNode;
import org.comroid.uniform.node.UniObjectNode;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.io.Closeable;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

public final class Gateway implements ContextualProvider.Underlying, Closeable {
    private static final Logger logger = LogManager.getLogger();
    public final @Internal
    Reference<Integer> heartbeatTime = Reference.create();
    public final @Internal
    FutureReference<ReadyEvent> readyEvent;
    private final Websocket socket;
    private final Pipe<? extends UniNode> dataPipeline;
    private final Pipe<? extends GatewayEvent> eventPipeline;
    private final DiscordBotShard shard;

    public Pipe<? extends WebsocketPacket> getPacketPipeline() {
        return socket.getPacketPipeline();
    }

    public Pipe<? extends GatewayEvent> getEventPipeline() {
        return eventPipeline;
    }

    @Override
    public ContextualProvider getUnderlyingContextualProvider() {
        return shard.plus(this);
    }

    @Internal
    public Gateway(DiscordBotShard shard, Websocket socket) {
        this.shard = shard;
        this.socket = socket;
        this.dataPipeline = getPacketPipeline()
                .filter(packet -> packet.getType() == WebsocketPacket.Type.DATA)
                .flatMap(WebsocketPacket::getData)
                .map(DiscordAPI.SERIALIZATION::parse);
        this.eventPipeline = dataPipeline
                .map(data -> {
                    OpCode op = IntEnum.valueOf(data.get("op").asInt(), OpCode.class)
                            .assertion(MessageSupplier.format("Invalid OP Code in data: %s", data));
                    logger.trace("Attempting to dispatch message as {}: {}", op.getName(), data);
                    return dispatchPacket(data, op);
                })
                .peek(event -> logger.trace("Gateway Event initialized: [{}] {}",
                        event.getClass().getSimpleName(), event));

        if (!(eventPipeline instanceof Pump))
            throw new AssertionError("eventPipeline is not a Pump");

        try {
            getEventPipeline()
                    .peek(System.out::println)
                    .flatMap(HelloEvent.class)
                    .next()
                    .thenAccept(hello -> {
                        final int interval = hello.heartbeatInterval.assertion("missing heartbeat value");
                        startHeartbeat(interval);
                    })
                    .thenCompose(ref -> sendIdentify(shard.getCurrentShardID()))
                    .join();
        } catch (Throwable t) {
            throw new RuntimeException("Could not send Identify", t);
        }
        // store first ready event
        this.readyEvent = new FutureReference<>(getEventPipeline()
                .flatMap(ReadyEvent.class)
                .next()
                .thenApply(ready -> {
                    logger.debug("Ready Event received: " + ready);
                    return ready;
                })
                .exceptionally(shard.context.exceptionLogger(logger, Level.FATAL, "Could not receive READY Event")));
    }

    private void startHeartbeat(int interval) {
        logger.debug("Shard {} - Started heartbeating at interval: {}", shard.getCurrentShardID(), interval);
        heartbeatTime.set(interval);

        requireFromContext(ScheduledExecutorService.class).scheduleAtFixedRate(
                () -> sendHeartbeat().join(),
                interval,
                interval,
                TimeUnit.MILLISECONDS);
    }

    public String getSessionID() {
        return readyEvent.flatMap(event -> event.sessionID).assertion();
    }

    private GatewayEvent dispatchPacket(UniNode data, OpCode opCode) {
        final UniNode innerData = data.get("d").asObjectNode();
        switch (opCode) {
            case DISPATCH:
                final DispatchEventType dispatchEventType = DispatchEventType.find(data)
                        .orElseThrow(() -> new NoSuchElementException("Unknown Dispatch Event: " + data.toString()));
                logger.trace("Handling Dispatch event as {} with data {}", dispatchEventType, innerData);
                return dispatchEventType.createPayload(shard, innerData.asObjectNode());
            case IDENTIFY:
            case RESUME:
            case HEARTBEAT:
            case REQUEST_GUILD_MEMBERS:
            case VOICE_STATE_UPDATE:
            case PRESENCE_UPDATE:
                // client sent only, unhandled
                logger.trace("Nothing to do here");
                return null;
            case RECONNECT:
                return new ReconnectEvent(shard, innerData.asObjectNode());
            case INVALID_SESSION:
                return new InvalidSessionEvent(shard, innerData.asObjectNode());
            case HELLO:
                return new HelloEvent(shard, innerData.asObjectNode());
            case HEARTBEAT_ACK:
                return new HeartbeatAckEvent(shard, innerData.asObjectNode());
        }

        throw new AssertionError("unreachable");
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Internal
    public CompletableFuture<UniNode> sendHeartbeat() {
        return socket.send(createPayloadBase(OpCode.HEARTBEAT).toString()).thenCompose(this::awaitAck);
    }

    @Internal
    public CompletableFuture<ReadyEvent> sendIdentify(int shardID) {
        final UniObjectNode data = createPayloadBase(OpCode.IDENTIFY);

        data.put("token", ValueType.STRING, "Bot " + shard.getToken());

        UniObjectNode prop = data.putObject("properties");
        prop.put("$os", ValueType.STRING, System.getProperty("os.name"));
        prop.put("$browser", ValueType.STRING, CrystalShard.toString);
        prop.put("$device", ValueType.STRING, shard.getFromContext(AbstractDiscordBot.class)
                .ifPresentMapOrElseGet(bot -> bot.getClass().getSimpleName(), () -> "CrystalShard"));

        data.put("shard", ValueType.INTEGER, shardID);

        return socket.send(data.toString()).thenCompose(socket -> getEventPipeline().flatMap(ReadyEvent.class).next());
    }

    private CompletableFuture<UniNode> awaitAck(Websocket socket) {
        return CompletableFuture.supplyAsync(() -> {
            CompletableFuture<? extends UniNode> next = dataPipeline
                    .filter(OpCode.HEARTBEAT_ACK)
                    .next();
            UniNode result;

            try {
                result = next.get(heartbeatTime.requireNonNull("Heartbeat Time unavailable") * 2, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException("Unexpected Exception awaiting HEARTBEAT_ACK", e);
            }

            return result;
        });
    }

    public UniObjectNode createPayloadBase(OpCode code) {
        return code.apply(DiscordAPI.SERIALIZATION.createUniObjectNode());
    }
}
