package de.kaleidox.crystalshard.main.items.channel;

import de.kaleidox.crystalshard.core.net.request.Endpoint;
import de.kaleidox.crystalshard.core.net.request.Method;
import de.kaleidox.crystalshard.core.net.request.WebRequest;
import de.kaleidox.crystalshard.internal.items.channel.PrivateTextChannelInternal;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.crystalshard.main.util.ChannelContainer;

import java.util.concurrent.CompletableFuture;

public interface PrivateTextChannel extends PrivateChannel, TextChannel {
    static CompletableFuture<PrivateTextChannel> of(ChannelContainer in, long id) {
        CompletableFuture<PrivateTextChannel> future = new CompletableFuture<>();

        if (in instanceof Discord) {
            Discord discord = (Discord) in;

            future = discord.getChannelById(id)
                    .map(PrivateTextChannel.class::cast)
                    .map(CompletableFuture::completedFuture)
                    .orElseGet(() -> new WebRequest<PrivateTextChannel>(discord)
                            .method(Method.GET)
                            .endpoint(Endpoint.of(Endpoint.Location.CHANNEL, id))
                            .execute(node -> PrivateTextChannelInternal.getInstance(discord, node))
                            .thenApply(PrivateTextChannel.class::cast)
                    );
        }

        return future;
    }
}
