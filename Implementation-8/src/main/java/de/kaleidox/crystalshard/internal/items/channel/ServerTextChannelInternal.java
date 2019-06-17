package de.kaleidox.crystalshard.internal.items.channel;

import com.fasterxml.jackson.databind.JsonNode;

import de.kaleidox.crystalshard.api.Discord;
import de.kaleidox.crystalshard.api.entity.channel.Channel;
import de.kaleidox.crystalshard.api.entity.channel.ChannelCategory;
import de.kaleidox.crystalshard.api.entity.channel.ServerChannel;
import de.kaleidox.crystalshard.api.entity.channel.ServerTextChannel;
import de.kaleidox.crystalshard.api.entity.permission.Permission;
import de.kaleidox.crystalshard.api.entity.permission.PermissionOverride;
import de.kaleidox.crystalshard.api.entity.server.Server;
import de.kaleidox.crystalshard.api.entity.server.interactive.MetaInvite;
import de.kaleidox.crystalshard.api.entity.user.User;
import de.kaleidox.crystalshard.api.exception.DiscordPermissionException;
import de.kaleidox.crystalshard.api.handling.editevent.EditTrait;
import de.kaleidox.crystalshard.core.CoreInjector;
import de.kaleidox.crystalshard.core.net.request.HttpMethod;
import de.kaleidox.crystalshard.core.net.request.WebRequest;
import de.kaleidox.crystalshard.core.net.request.endpoint.DiscordEndpoint;
import de.kaleidox.crystalshard.internal.items.permission.PermissionOverrideInternal;
import de.kaleidox.crystalshard.internal.items.server.interactive.InviteInternal;
import de.kaleidox.util.helpers.FutureHelper;
import de.kaleidox.util.helpers.ListHelper;
import de.kaleidox.util.helpers.OptionalHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static de.kaleidox.crystalshard.api.handling.editevent.enums.ChannelEditTrait.NAME;
import static de.kaleidox.crystalshard.api.handling.editevent.enums.ChannelEditTrait.NSFW_FLAG;
import static de.kaleidox.crystalshard.api.handling.editevent.enums.ChannelEditTrait.PERMISSION_OVERWRITES;
import static de.kaleidox.crystalshard.api.handling.editevent.enums.ChannelEditTrait.TOPIC;

public class ServerTextChannelInternal extends TextChannelInternal implements ServerTextChannel {
    final static ConcurrentHashMap<Long, ServerTextChannel> instances = new ConcurrentHashMap<>();
    final List<PermissionOverride> overrides;
    final Server server;
    boolean isNsfw;
    String topic;
    String name;
    ChannelCategory category;

    public ServerTextChannelInternal(Discord discord, Server server, JsonNode data) {
        super(discord, data);
        this.server = server;
        this.overrides = new ArrayList<>();
        updateData(data);

        data.path("permission_overwrites")
                .forEach(node -> overrides.add(new PermissionOverrideInternal(discord, server, node)));

        instances.put(id, this);
    }

    // Override Methods
    @Override
    public Set<EditTrait<Channel>> updateData(JsonNode data) {
        Set<EditTrait<Channel>> traits = new HashSet<>();

        if (isNsfw != data.path("nsfw")
                .asBoolean(isNsfw)) {
            isNsfw = data.get("nsfw")
                    .asBoolean();
            traits.add(NSFW_FLAG);
        }
        if (topic == null || !topic.equals(data.path("topic")
                .asText(topic))) {
            topic = data.get("topic")
                    .asText();
            traits.add(TOPIC);
        }
        if (name == null || !name.equals(data.path("name")
                .asText(name))) {
            name = data.get("name")
                    .asText();
            traits.add(NAME);
        }
        //noinspection ConstantConditions
        if (category == null || (this.category == null && data.has("parent_id"))) {
            long parentId = data.path("parent_id")
                    .asLong(-1);
            this.category = parentId == -1 ? null : discord.getChannelCache()
                    .getOrRequest(parentId, parentId)
                    .toChannelCategory()
                    .orElse(null);
        } else //noinspection ConstantConditions
            if (this.category != null && !data.has("parent_id")) {
                this.category = null;
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
    public CompletableFuture<Collection<MetaInvite>> getChannelInvites() {
        if (!hasPermission(discord, Permission.MANAGE_CHANNELS))
            return FutureHelper.failedFuture(new DiscordPermissionException(
                    "Cannot get channel invite!",
                    Permission.MANAGE_CHANNELS));
        WebRequest<Collection<MetaInvite>> request = CoreInjector.webRequest(discord);
        return request.setMethod(HttpMethod.GET)
                .setUri(DiscordEndpoint.CHANNEL_INVITE.createUri(id))
                .executeAs(data -> {
                    List<MetaInvite> list = new ArrayList<>();
                    data.forEach(invite -> list.add(new InviteInternal.Meta(discord, invite)));
                    return list;
                });
    }

    @Override
    public InviteBuilder getInviteBuilder() {
        return new ChannelBuilderInternal.ChannelInviteBuilder(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPosition() {
        return 0; // todo
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public boolean isNsfw() {
        return isNsfw;
    }

    @Override
    public ServerTextChannel.Updater getUpdater() {
        return new ChannelUpdaterInternal.ServerTextChannelUpdater(discord, this);
    }

    @Override
    public boolean hasPermission(User user, Permission permission) {
        return OptionalHelper.or(overrides.stream()
                .filter(override -> override.getParent() != null)
                .filter(override -> override.getParent()
                        .equals(user))
                .map(override -> override.getAllowed()
                        .contains(permission))
                .findAny(), () -> OptionalHelper.or(this.getCategory()
                .flatMap(channelCategory -> channelCategory.getPermissionOverrides()
                        .stream()
                        .filter(override -> override.getParent() != null)
                        .filter(override -> override.getParent()
                                .equals(user))
                        .findAny())
                .map(override -> override.getAllowed()
                        .contains(permission)), () -> Optional.of(toServerChannel().map(ServerChannel::getServer)
                .orElseThrow(AssertionError::new)
                .getEveryoneRole()
                .getPermissions()
                .contains(Permission.SEND_MESSAGES))))
                .orElse(true); // if no information could be found, assert TRUE
    }
}