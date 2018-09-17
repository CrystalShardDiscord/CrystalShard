package de.kaleidox.crystalshard.core.net.socket;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.kaleidox.crystalshard.core.concurrent.ThreadPool;
import de.kaleidox.crystalshard.core.net.request.Endpoint;
import de.kaleidox.crystalshard.core.net.request.Method;
import de.kaleidox.crystalshard.core.net.request.WebRequest;
import de.kaleidox.crystalshard.internal.DiscordInternal;
import de.kaleidox.crystalshard.main.CrystalShard;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.logging.Logger;
import de.kaleidox.util.helpers.FutureHelper;
import de.kaleidox.util.helpers.JsonHelper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class WebSocketClient {
    private static final Logger          logger        = new Logger(WebSocketClient.class);
    private static final HttpClient      CLIENT        = HttpClient.newHttpClient();
    private final        DiscordInternal discord;
    private final        WebSocket       webSocket;
    private final        AtomicLong      lastPacket    = new AtomicLong(0);
    private final        AtomicLong      lastHeartbeat = new AtomicLong(0);
    private final        ThreadPool      threadPool;
    
    public WebSocketClient(Discord discordObject) {
        URI gatewayUrl = new WebRequest<String>(discordObject).method(Method.GET)
                .endpoint(Endpoint.Location.GATEWAY.toEndpoint())
                .execute(node -> node.get("url").asText())
                .exceptionally(throwable -> {
                    logger.exception(throwable);
                    return "wss://gateway.discord.gg"; // default gateway if gateway couldn't be retrieved
                })
                .thenApply(URI::create)
                .join();
        this.threadPool = new ThreadPool(discordObject, 1, "WebSocketClient");
        this.discord = (DiscordInternal) discordObject;
        this.webSocket = CLIENT.newWebSocketBuilder()
                .header("Authorization", discordObject.getPrefixedToken())
                .buildAsync(gatewayUrl, new WebSocketListener((DiscordInternal) discordObject))
                .join();
        identification();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> webSocket.sendClose(1000, "Shutting down!")));
    }
    
    public CompletableFuture<WebSocket> sendPayload(Payload payload) {
        assert payload != null : "Payload must not be null!";
        CompletableFuture<WebSocket> future = new CompletableFuture<>();
        this.threadPool.execute(() -> {
            while (lastPacket.get() > (System.currentTimeMillis() - 600)) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                    logger.exception(e);
                }
            }
            if (lastPacket.get() <= (System.currentTimeMillis() - 500)) {
                logger.trace("Sending Packet with OpCode " + payload.getCode() + " and body: " + payload.getBody());
                CompletableFuture<WebSocket> subFut;
                boolean attachedFuture = false;
                for (Payload that : payload.split()) {
                    boolean last = that.isLast();
                    that.addNode("token", JsonHelper.nodeOf(discord.getPrefixedToken()));
                    that.addNode("large_threshold", JsonHelper.nodeOf(250));
                    that.addNode("shard", JsonHelper.arrayNode(discord.getShardId(), discord.getShards()));
                    CharSequence nodeAsText = that.getSendableNode();
                    subFut = webSocket.sendText(nodeAsText, last);
                    if (last) lastPacket.set(System.currentTimeMillis());
                    if (!attachedFuture) {
                        FutureHelper.linkFutures(subFut, future);
                        attachedFuture = true;
                    }
                }
            }
        });
        return future;
    }
    
    private void identification() {
        ObjectNode data = JsonHelper.objectNode("properties",
                                                JsonHelper.objectNode("$os",
                                                                      JsonHelper.nodeOf(System.getProperty("os.name")),
                                                                      "$browser",
                                                                      JsonHelper.nodeOf(CrystalShard.SHORT_FOOTPRINT),
                                                                      "$device",
                                                                      JsonHelper.nodeOf(CrystalShard.SHORT_FOOTPRINT)));
        sendPayload(Payload.create(OpCode.IDENTIFY, data)).exceptionally(logger::exception);
    }
    
    public void heartbeat() {
        Payload payload = Payload.create(OpCode.HEARTBEAT, JsonHelper.nodeOf(null));
        sendPayload(payload);
        lastHeartbeat.set(System.currentTimeMillis());
    }
    
    public boolean respondToHeartbeat() {
        if (lastHeartbeat.get() < System.currentTimeMillis() - 4000) {
            return true;
        }
        return false;
    }
}
