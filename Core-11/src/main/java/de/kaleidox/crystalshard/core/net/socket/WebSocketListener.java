package de.kaleidox.crystalshard.core.net.socket;

import de.kaleidox.crystalshard.logging.Logger;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.crystalshard.util.helpers.JsonHelper;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class WebSocketListener implements WebSocket.Listener {
    private final static Logger logger = new Logger(WebSocketListener.class);
    private final Discord discord;
    private StringBuilder onTextBuilder = new StringBuilder();
    private CompletableFuture<String> onTextFuture = new CompletableFuture<>();

    public WebSocketListener(Discord discordObject) {
        this.discord = discordObject;
    }

    // Override Methods
    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
        logger.trace("WebSocket opened: " + webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        webSocket.request(1);
        logger.exception(error, "WebSocket error");
    }

    @Override
    public CompletionStage<String> onText(WebSocket webSocket, CharSequence data, boolean last) {
        webSocket.request(1);
        onTextBuilder.append(data);
        if (last) {
            onTextFuture.complete(onTextBuilder.toString());
            onTextBuilder = new StringBuilder();
            CompletableFuture<String> returning = onTextFuture;
            returning.thenAcceptAsync(logger::deeptrace);
            returning.exceptionally(logger::exception)
                    .thenApplyAsync(JsonHelper::parse)
                    .thenAcceptAsync(node -> DiscordEventDispatch.handle(discord, node));
            onTextFuture = new CompletableFuture<>();
            return returning;
        }
        return onTextFuture;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        webSocket.request(1);
        logger.error("WebSocket#onBinary is not implemented! Please contact the developer.");
        return null;
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        webSocket.request(1);
        logger.error("WebSocket#onPing is not implemented! Please contact the developer.");
        return null;
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        webSocket.request(1);
        logger.error("WebSocket#onPong is not implemented! Please contact the developer.");
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        webSocket.request(1);
        logger.trace("WebSocket closed with code \'" + statusCode + "\' and reason: " + reason);
        return null;
    }
}
