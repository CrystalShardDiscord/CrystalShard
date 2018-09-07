package de.kaleidox.crystalshard.internal.core.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import de.kaleidox.crystalshard.internal.DiscordInternal;
import de.kaleidox.crystalshard.internal.event.message.MessageCreateEventInternal;
import de.kaleidox.crystalshard.internal.items.channel.PrivateTextChannelInternal;
import de.kaleidox.crystalshard.internal.items.channel.ServerTextChannelInternal;
import de.kaleidox.crystalshard.main.handling.listener.channel.ChannelAttachableListener;
import de.kaleidox.crystalshard.main.handling.listener.message.MessageCreateListener;
import de.kaleidox.crystalshard.main.items.channel.PrivateTextChannel;
import de.kaleidox.crystalshard.main.items.channel.ServerTextChannel;
import de.kaleidox.crystalshard.main.items.message.Message;

import java.util.ArrayList;
import java.util.List;

public class MESSAGE_CREATE extends HandlerBase {
    @Override
    public void handle(DiscordInternal discord, JsonNode data) {
        discord.getChannelById(data.path("channel_id").asLong(-1))
                .ifPresentOrElse(channel -> {
                    Message message = null;
                    List<ChannelAttachableListener> listeners = new ArrayList<>();
                    if (channel instanceof PrivateTextChannel) {
                        message = ((PrivateTextChannelInternal) channel).craftMessage(data);
                        listeners.addAll(((PrivateTextChannelInternal) channel).getListeners());
                    } else if (channel instanceof ServerTextChannel) {
                        message = ((ServerTextChannelInternal) channel).craftMessage(data);
                        listeners.addAll(((ServerTextChannelInternal) channel).getListeners());
                    }
                    Message finalMessage = message;
                    discord.getListenerManangers()
                            .stream()
                            .filter(listener -> MessageCreateListener.class
                                    .isAssignableFrom(listener.getClass()))
                            .map(MessageCreateListener.class::cast)
                            .forEach(listener -> discord.getThreadPool()
                                    .execute(() -> {
                                        assert finalMessage != null;
                                        listener.onMessageCreate(
                                                new MessageCreateEventInternal(
                                                        discord,
                                                        finalMessage
                                                )
                                        );
                                    }));
                    listeners.stream()
                            .filter(listener -> MessageCreateListener.class
                                    .isAssignableFrom(listener.getClass()))
                            .map(MessageCreateListener.class::cast)
                            .forEach(listener -> discord.getThreadPool()
                                    .execute(() -> {
                                        assert finalMessage != null;
                                        listener.onMessageCreate(
                                                new MessageCreateEventInternal(
                                                        discord,
                                                        finalMessage
                                                )
                                        );
                                    }));
                }, () -> {

                });
    }
}
