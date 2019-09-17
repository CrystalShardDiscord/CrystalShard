package de.kaleidox.crystalshard.core.api.gateway.event.message.reaction;

// https://discordapp.com/developers/docs/topics/gateway#message-reaction-add

import java.util.Optional;

import de.kaleidox.crystalshard.api.entity.channel.TextChannel;
import de.kaleidox.crystalshard.api.entity.emoji.Emoji;
import de.kaleidox.crystalshard.api.entity.guild.Guild;
import de.kaleidox.crystalshard.api.entity.message.Message;
import de.kaleidox.crystalshard.api.entity.user.User;
import de.kaleidox.crystalshard.core.api.gateway.event.GatewayEvent;

public interface MessageReactionAddEvent extends GatewayEvent {
    String NAME = "MESSAGE_REACTION_ADD";

    User getUser();

    TextChannel getChannel();

    Message getMessage();

    Optional<Guild> getGuild();

    Emoji getEmoji();
}