package de.kaleidox.crystalshard.core.api.gateway.event.guild.member;

// https://discordapp.com/developers/docs/topics/gateway#guild-members-chunk

import java.util.Collection;

import de.kaleidox.crystalshard.api.entity.guild.Guild;
import de.kaleidox.crystalshard.api.entity.user.GuildMember;
import de.kaleidox.crystalshard.core.api.gateway.event.GatewayEvent;

public interface GuildMembersChunkEvent extends GatewayEvent {
    String NAME = "GUILD_MEMBERS_CHUNK";

    Guild getGuild();

    Collection<GuildMember> getMembers();
}