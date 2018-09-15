package de.kaleidox.crystalshard.internal.items.channel;

import com.fasterxml.jackson.databind.JsonNode;
import de.kaleidox.crystalshard.internal.items.permission.PermissionOverrideInternal;
import de.kaleidox.crystalshard.internal.items.server.ServerInternal;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.crystalshard.main.handling.editevent.EditTrait;
import de.kaleidox.crystalshard.main.handling.editevent.enums.ChannelEditTrait;
import de.kaleidox.crystalshard.main.items.channel.Channel;
import de.kaleidox.crystalshard.main.items.channel.ChannelCategory;
import de.kaleidox.crystalshard.main.items.channel.ServerVoiceChannel;
import de.kaleidox.crystalshard.main.items.permission.PermissionOverride;
import de.kaleidox.crystalshard.main.items.server.Server;
import de.kaleidox.util.helpers.ListHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static de.kaleidox.crystalshard.main.handling.editevent.enums.ChannelEditTrait.*;

public class ServerVoiceChannelInternal extends VoiceChannelInternal implements ServerVoiceChannel {
    final static ConcurrentHashMap<Long, ServerVoiceChannel> instances = new ConcurrentHashMap<>();
    final List<PermissionOverride> overrides;
    final Server server;
    String name;
    ChannelCategory category;

    private ServerVoiceChannelInternal(Discord discord, Server server, JsonNode data) {
        super(discord, data);
        this.server = server;
        this.name = data.path("name").asText("");
        this.category = ChannelInternal.getInstance(discord, data.path("parent_id").asLong(0))
                .toChannelCategory()
                .orElse(null);

        this.overrides = new ArrayList<>();
        data.path("permission_overwrites")
                .forEach(node -> overrides.add(new PermissionOverrideInternal(discord, server, node)));

        instances.put(id, this);
    }

    @Override
    public Set<EditTrait<Channel>> updateData(JsonNode data) {
        Set<EditTrait<Channel>> traits = new HashSet<>();

        if (bitrate != data.path("bitrate").asInt(bitrate)) {
            bitrate = data.get("bitrate").asInt();
            traits.add(BITRATE);
        }
        if (limit != data.path("limit").asInt(limit)) {
            limit = data.get("limit").asInt();
            traits.add(USER_LIMIT);
        }
        if (this.category == null && data.has("parent_id")) {
            ChannelCategory category = ChannelInternal.getInstance(discord, data.path("parent_id").asLong(
                    this.category == null ? 0 : this.category.getId()))
                    .toChannelCategory()
                    .orElse(null);
            if (!this.category.equals(category)) {
                this.category = category;
                traits.add(CATEGORY);
            }
        }
        if (!name.equals(data.path("name").asText(name))) {
            name = data.get("name").asText();
            traits.add(NAME);
        }
        List<PermissionOverride> overrides = new ArrayList<>();
        data.path("permission_overwrites")
                .forEach(node -> overrides.add(new PermissionOverrideInternal(discord, server, node)));
        if (!ListHelper.equalContents(overrides, this.overrides)) {
            this.overrides.clear();
            this.overrides.addAll(overrides);
            traits.add(PERMISSION_OVERWRITES);
        }

        return traits;
    }

    public static ServerVoiceChannel getInstance(Discord discord, Server server, JsonNode data) {
        long id = data.get("id").asLong(-1);
        if (id == -1) throw new NoSuchElementException("No valid ID found.");
        if (server == null) server = ServerInternal.getInstance(discord, data.path("guild_id").asLong(0));
        if (instances.containsKey(id))
            return instances.get(id);
        else
            return new ServerVoiceChannelInternal(discord, server, data);
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public Optional<ChannelCategory> getCategory() {
        return Optional.ofNullable(category);
    }

    @Override
    public List<PermissionOverride> getPermissionOverrides() {
        return overrides;
    }

    @Override
    public String getName() {
        return name;
    }
}
