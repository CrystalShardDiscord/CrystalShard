package de.comroid.crystalshard.api.listener.message;

import de.comroid.crystalshard.adapter.Adapter;
import de.comroid.crystalshard.api.Discord;
import de.comroid.crystalshard.api.event.message.MessageSentEvent;
import de.comroid.crystalshard.api.listener.DiscordAttachableListener;
import de.comroid.crystalshard.api.listener.channel.ChannelAttachableListener;
import de.comroid.crystalshard.api.listener.guild.GuildAttachableListener;
import de.comroid.crystalshard.api.listener.model.ListenerManager;
import de.comroid.crystalshard.api.listener.role.RoleAttachableListener;
import de.comroid.crystalshard.api.listener.user.UserAttachableListener;
import de.comroid.crystalshard.core.api.gateway.Gateway;
import de.comroid.crystalshard.core.api.gateway.event.message.MessageCreateEvent;
import de.comroid.crystalshard.core.api.gateway.listener.message.MessageCreateListener;
import de.comroid.crystalshard.util.annotation.InitializedBy;

@InitializedBy(MessageSentListener.Initializer.class)
public interface MessageSentListener extends
        DiscordAttachableListener<MessageSentEvent>,
        GuildAttachableListener<MessageSentEvent>,
        ChannelAttachableListener<MessageSentEvent>,
        RoleAttachableListener<MessageSentEvent>,
        UserAttachableListener<MessageSentEvent> {
    class Initializer implements ListenerManager.Initializer<MessageSentListener> {
        @Override
        public void initialize(Gateway gateway, MessageSentListener listener) {
            class Handler implements MessageCreateListener {
                private final MessageSentListener trigger = listener;
                private final Discord api = gateway.getAPI();

                @Override 
                public void onEvent(MessageCreateEvent event) {
                    trigger.onEvent(Adapter.create(MessageSentEvent.class, event.getAffected(), event.getMessage()));
                }
            }
            
            gateway.attachListener(new Handler());
        }
    }
}
