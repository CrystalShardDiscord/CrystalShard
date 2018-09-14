package de.kaleidox.crystalshard.internal.items.channel;

import com.fasterxml.jackson.databind.JsonNode;
import de.kaleidox.crystalshard.internal.items.permission.PermissionOverrideInternal;
import de.kaleidox.crystalshard.internal.items.server.ServerInternal;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.crystalshard.main.items.channel.ChannelCategory;
import de.kaleidox.crystalshard.main.items.channel.ServerTextChannel;
import de.kaleidox.crystalshard.main.items.permission.PermissionOverride;
import de.kaleidox.crystalshard.main.items.server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ServerTextChannelInternal extends TextChannelInternal implements ServerTextChannel {
    final static ConcurrentHashMap<Long, ServerTextChannel> instances = new ConcurrentHashMap<>();
    final boolean isNsfw;
    final String topic;
    final String name;
    final List<PermissionOverride> overrides;
    final Server server;
    final ChannelCategory category;

    private ServerTextChannelInternal(Discord discord, Server server, JsonNode data) {
        super(discord, data);
        this.server = server;
        this.name = data.path("name").asText("");
        this.isNsfw = data.path("nsfw").asBoolean(false);
        this.topic = data.path("topic").asText(null);
        this.category = ChannelInternal.getInstance(discord, data.path("parent_id").asInt(0))
                .toChannelCategory()
                .orElse(null);

        this.overrides = new ArrayList<>();
        data.path("permission_overwrites")
                .forEach(node -> overrides.add(new PermissionOverrideInternal(discord, server, node)));

        instances.put(id, this);
    }

    public static ServerTextChannel getInstance(Discord discord, Server server, JsonNode data) {
        long id = data.get("id").asLong(-1);
        if (id == -1) throw new NoSuchElementException("No valid ID found.");
        if (server == null) server = ServerInternal.getInstance(discord, data.path("guild_id").asLong(0));
        if (instances.containsKey(id))
            return instances.get(id);
        else
            return new ServerTextChannelInternal(discord, server, data);
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

    @Override
    public String getTopic() {
        return topic;
    }
}
