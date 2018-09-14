package de.kaleidox.crystalshard.internal.items.permission;

import com.fasterxml.jackson.databind.JsonNode;
import de.kaleidox.crystalshard.internal.items.role.RoleInternal;
import de.kaleidox.crystalshard.internal.items.user.UserInternal;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.crystalshard.main.items.permission.*;
import de.kaleidox.crystalshard.main.items.server.Server;
import de.kaleidox.util.helpers.JsonHelper;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static de.kaleidox.crystalshard.main.items.permission.OverrideState.ALLOWED;
import static de.kaleidox.crystalshard.main.items.permission.OverrideState.DENIED;

public class PermissionOverrideInternal
        extends ConcurrentHashMap<Permission, OverrideState>
        implements PermissionOverride {
    private final Discord discord;
    private final Server server;
    private final Type type;
    private final PermissionOverwritable parent;

    public PermissionOverrideInternal(Discord discord, Server server, JsonNode data) {
        super();
        this.discord = discord;
        this.server = server;
        this.type = Type.getByKey(data.get("type").asText());
        switch (type) {
            default:
                throw new AssertionError();
            case ROLE:
                this.parent = RoleInternal.getInstance(server, data.get("id").asLong());
                break;
            case USER:
                this.parent = UserInternal.getInstance(discord, data.get("id").asLong())
                        .toServerMember(server);
                break;
        }
        new PermissionListInternal(data.get("allow").asInt(0))
                .forEach(permission -> put(permission, ALLOWED));
        new PermissionListInternal(data.get("deny").asInt(0))
                .forEach(permission -> put(permission, DENIED));
    }

    @Override
    public Discord getDiscord() {
        return discord;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public Type getOverrideType() {
        return type;
    }

    @Override
    public PermissionOverwritable getParent() {
        return parent;
    }

    @Override
    public PermissionOverride addOverride(Permission permission, OverrideState state) {
        removeOverride(permission);
        put(permission, state);
        return this;
    }

    @Override
    public PermissionOverride removeOverride(Permission permission) {
        remove(permission);
        return this;
    }

    @Override
    public PermissionList getAllowed() {
        return entrySet()
                .stream()
                .filter(entry -> entry.getValue() == ALLOWED)
                .map(Entry::getKey)
                .collect(Collectors.toCollection(() -> PermissionList.create(parent)));
    }

    @Override
    public PermissionList getDenied() {
        return entrySet()
                .stream()
                .filter(entry -> entry.getValue() == DENIED)
                .map(Entry::getKey)
                .collect(Collectors.toCollection(() -> PermissionList.create(parent)));
    }

    public JsonNode toJsonNode() {
        return JsonHelper.objectNode(
                "id", Long.toUnsignedString(Objects.requireNonNull(parent).getId()),
                "type", type.getKey(),
                "allow", getAllowed().toPermissionInt(),
                "deny", getDenied().toPermissionInt()
        );
    }
}
