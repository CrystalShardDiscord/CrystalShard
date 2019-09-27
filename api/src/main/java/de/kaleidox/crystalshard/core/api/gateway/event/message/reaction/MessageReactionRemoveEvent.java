package de.kaleidox.crystalshard.core.api.gateway.event.message.reaction;

// https://discordapp.com/developers/docs/topics/gateway#message-reaction-remove

import java.util.Optional;

import de.kaleidox.crystalshard.api.entity.channel.TextChannel;
import de.kaleidox.crystalshard.api.entity.emoji.CustomEmoji;
import de.kaleidox.crystalshard.api.entity.emoji.Emoji;
import de.kaleidox.crystalshard.api.entity.guild.Guild;
import de.kaleidox.crystalshard.api.entity.message.Message;
import de.kaleidox.crystalshard.api.entity.user.User;
import de.kaleidox.crystalshard.core.api.gateway.event.GatewayEvent;
import de.kaleidox.crystalshard.core.api.gateway.listener.message.reaction.MessageReactionRemoveListener;
import de.kaleidox.crystalshard.util.annotation.ManagedBy;

@ManagedBy(MessageReactionRemoveListener.Manager.class)
public interface MessageReactionRemoveEvent extends GatewayEvent {
    String NAME = "MESSAGE_REACTION_REMOVE";

    User getUser();

    TextChannel getChannel();

    Message getMessage();

    Optional<Guild> getGuild();

    CustomEmoji getEmoji();
}
