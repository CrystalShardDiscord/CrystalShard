package org.comroid.crystalshard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.comroid.api.ContextualProvider;
import org.comroid.crystalshard.entity.user.User;
import org.comroid.crystalshard.gateway.Gateway;
import org.comroid.crystalshard.gateway.event.GatewayEvent;
import org.comroid.crystalshard.rest.Endpoint;
import org.comroid.crystalshard.rest.response.AbstractRestResponse;
import org.comroid.mutatio.pipe.Pipe;
import org.comroid.mutatio.ref.FutureReference;
import org.comroid.restless.HttpAdapter;
import org.comroid.restless.REST;
import org.comroid.restless.socket.WebsocketPacket;
import org.comroid.uniform.SerializationAdapter;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public final class DiscordBotShard implements Bot {
    private static final Logger logger = LogManager.getLogger();
    public final DiscordAPI context;
    private final String token;
    private final int currentShardID;
    private final FutureReference<? extends Gateway> gateway;
    @Internal
    public final List<Consumer<DiscordBotShard>> readyTasks = new ArrayList<>();

    @Override
    public SnowflakeCache getSnowflakeCache() {
        return context.getSnowflakeCache();
    }

    public Pipe<? extends WebsocketPacket> getPacketPipeline() {
        return gateway.into(Gateway::getPacketPipeline);
    }

    @Override
    public Pipe<? extends GatewayEvent> getEventPipeline() {
        return gateway.into(Gateway::getEventPipeline);
    }

    @Override
    public ContextualProvider getUnderlyingContextualProvider() {
        return context.plus(this);
    }

    @Override
    public boolean isReady() {
        return gateway.future.isDone() && getGateway().readyEvent.future.isDone();
    }

    public Gateway getGateway() {
        return gateway.assertion();
    }

    @Override
    public User getYourself() {
        return gateway.flatMap(gateway -> gateway.readyEvent)
                .flatMap(readyEvent -> readyEvent.yourself)
                .assertion();
    }

    @Override
    public int getCurrentShardID() {
        return currentShardID;
    }

    @Override
    public int getShardCount() {
        return gateway.flatMap(gateway -> gateway.readyEvent)
                .flatMap(readyEvent -> readyEvent.shard)
                .map(array -> array.get(1))
                .assertion();
    }

    @Override
    public <R extends AbstractRestResponse> CompletableFuture<R> newRequest(REST.Method method, Endpoint<R> endpoint) {
        return DiscordAPI.newRequest(context, token, method, endpoint);
    }

    @Override
    public String getToken() {
        return token;
    }

    public DiscordBotShard(DiscordAPI context, String token, URI wsUri, int shardID) {
        context.plus(this);
        this.context = context;
        this.token = token;
        this.currentShardID = shardID;

        HttpAdapter httpAdapter = requireFromContext(HttpAdapter.class);
        SerializationAdapter serializationAdapter = requireFromContext(SerializationAdapter.class);
        ScheduledExecutorService executor = requireFromContext(ScheduledExecutorService.class);
        this.gateway = new FutureReference<>(httpAdapter.createWebSocket(executor, wsUri, DiscordAPI.createHeaders(token))
                        .thenApply(socket -> new Gateway(this, socket)));

        gateway.into(gtw -> gtw.readyEvent).future
                .thenRun(() -> {
                    readyTasks.forEach(task -> task.accept(this));
                    logger.info(String.format("%s - Shard %d is ready!", toString(), getCurrentShardID()));
                });
    }

    @Override
    public void close() throws IOException {
        gateway.future.join().close();
    }

    protected void whenReady(Consumer<DiscordBotShard> readyTask) {
        if (isReady())
            readyTask.accept(this);
        readyTasks.add(readyTask);
    }

    @Override
    public String toString() {
        return String.format("DiscordBotShard<Shard %d / %d>", getCurrentShardID(), getShardCount());
    }
}
