package de.comroid.crystalshard.core.api.gateway.event.guild.role;

// https://discordapp.com/developers/docs/topics/gateway#guild-role-delete

import de.comroid.crystalshard.api.entity.guild.Guild;
import de.comroid.crystalshard.api.entity.guild.Role;
import de.comroid.crystalshard.core.api.gateway.event.GatewayEvent;
import de.comroid.crystalshard.core.api.gateway.listener.guild.role.GuildRoleDeleteListener;
import de.comroid.crystalshard.util.annotation.InitializedBy;

public interface GuildRoleDeleteEvent extends GatewayEvent {
    String NAME = "GUILD_ROLE_DELETE";

    Guild getGuild();

    Role getRole();
}
