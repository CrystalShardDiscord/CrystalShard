package org.comroid.crystalshard.core.gateway.event;

// https://discordapp.com/developers/docs/topics/gateway#guild-role-delete

import org.comroid.crystalshard.adapter.MainAPI;
import org.comroid.crystalshard.api.entity.guild.Guild;
import org.comroid.crystalshard.api.entity.guild.Role;
import org.comroid.crystalshard.core.cache.CacheManager;
import org.comroid.crystalshard.util.model.serialization.JSONBinding;
import org.comroid.crystalshard.util.model.serialization.JSONBindingLocation;

import static org.comroid.crystalshard.util.model.serialization.JSONBinding.cache;

@MainAPI
@JSONBindingLocation(GUILD_ROLE_DELETE.JSON.class)
public interface GUILD_ROLE_DELETE extends GatewayEventBase {
    default Guild getGuild() {
        return getBindingValue(JSON.GUILD);
    }

    default Role getRole() {
        return getBindingValue(JSON.ROLE);
    }
    
    interface JSON {
        JSONBinding.TwoStage<Long, Guild> GUILD = cache("guild_id", CacheManager::getGuildByID);
        JSONBinding.TwoStage<Long, Role> ROLE = cache("role_id", CacheManager::getRoleByID);
    }
}