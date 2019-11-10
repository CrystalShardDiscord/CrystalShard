package de.comroid.crystalshard.api.model.permission;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.comroid.crystalshard.adapter.Adapter;
import de.comroid.crystalshard.api.entity.Snowflake;
import de.comroid.crystalshard.api.entity.channel.GuildChannel;
import de.comroid.crystalshard.api.entity.guild.Role;
import de.comroid.crystalshard.api.entity.user.User;
import de.comroid.crystalshard.core.cache.Cacheable;
import de.comroid.crystalshard.util.model.serialization.JSONBinding;
import de.comroid.crystalshard.util.model.serialization.JsonDeserializable;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.Nullable;

import static de.comroid.crystalshard.util.model.serialization.JSONBinding.identity;
import static de.comroid.crystalshard.util.model.serialization.JSONBinding.simple;

public interface PermissionOverride extends JsonDeserializable {
    default <X extends Snowflake & Cacheable & PermissionOverridable> X getTarget() {
        long targetId = getBindingValue(JSON.TARGET_ID);

        return (X) (switch (getTargetType()) {
            case ROLE -> getAPI().getCacheManager()
                    .streamSnowflakesByID(Role.class, targetId);
            case MEMBER -> getAPI().getCacheManager()
                    .streamSnowflakesByID(User.class, targetId);
        }).findAny().orElseThrow();
    }

    default TargetType getTargetType() {
        return getBindingValue(JSON.TARGET_TYPE);
    }

    default int getAllowedBitmask() {
        return getBindingValue(JSON.ALLOWED);
    }

    default int getDeniedBitmask() {
        return getBindingValue(JSON.DENIED);
    }

    GuildChannel getChannel();

    default State getState(Permission permission) {
        final int allowedBitmask = getAllowedBitmask();
        final int deniedBitmask = getDeniedBitmask();

        // local var to save 1 bitwise operation TODO lol is this even worth?
        boolean isDenied;

        if (permission.isSet(allowedBitmask) & !(isDenied = permission.isSet(deniedBitmask)))
            return State.ALLOWED;
        else if (isDenied) return State.DENIED;

        return State.UNSET;
    }

    default State[] getStates(Permission... permissions) {
        State[] yields = new State[permissions.length];

        for (int i = 0; i < permissions.length; i++)
            yields[i] = getState(permissions[i]);

        return yields;
    }

    default boolean isTargetUser() {
        return getTarget().asGuildMember().isPresent();
    }

    default boolean isTargetRole() {
        return getTarget().asRole().isPresent();
    }

    default Set<Permission> getAllowedPermissions() {
        final int allowedBitmask = getAllowedBitmask();

        return Stream.of(Permission.values())
                .filter(permission -> permission.isSet(allowedBitmask))
                .collect(Collectors.toSet());
    }

    default Set<Permission> getDeniedPermissions() {
        final int deniedBitmask = getDeniedBitmask();

        return Stream.of(Permission.values())
                .filter(permission -> permission.isSet(deniedBitmask))
                .collect(Collectors.toSet());
    }

    default Set<Permission> getUnsetPermissions() {
        final int allowedBitmask = getAllowedBitmask();
        final int deniedBitmask = getDeniedBitmask();

        return Stream.of(Permission.values())
                .filter(permission -> !(permission.isSet(allowedBitmask) || permission.isSet(deniedBitmask)))
                .collect(Collectors.toSet());
    }

    default boolean isAllowed(Permission permission) {
        return getState(permission) == State.ALLOWED;
    }

    default boolean isDenied(Permission permission) {
        return getState(permission) == State.DENIED;
    }

    default boolean isUnset(Permission permission) {
        return getState(permission) == State.UNSET;
    }

    static Builder builder() {
        return Adapter.require(Builder.class);
    }

    static PermissionOverride fromBitmask(int allowedBitmask) {
        return fromBitmasks(allowedBitmask, 0);
    }

    static PermissionOverride fromBitmasks(int allowedBitmask, int deniedBitmask) {
        Builder builder = builder();

        for (Permission permissionType : Permission.values()) {
            if (permissionType.isSet(allowedBitmask) && permissionType.isSet(deniedBitmask))
                builder.setState(permissionType, State.UNSET);
            else if (permissionType.isSet(allowedBitmask))
                builder.setState(permissionType, State.ALLOWED);
            else if (permissionType.isSet(deniedBitmask))
                builder.setState(permissionType, State.DENIED);
            else builder.setState(permissionType, State.UNSET);
        }

        return builder.build();
    }

    interface JSON {
        JSONBinding.OneStage<Long> TARGET_ID = identity("id", JSONObject::getLong);
        JSONBinding.TwoStage<String, TargetType> TARGET_TYPE = simple("type", JSONObject::getString, TargetType::from);
        JSONBinding.OneStage<Integer> ALLOWED = identity("allow", JSONObject::getInteger);
        JSONBinding.OneStage<Integer> DENIED = identity("deny", JSONObject::getInteger);
    }

    interface Builder {
        State getState(Permission permission);

        Builder setState(Permission permission, State state);

        PermissionOverride build();

        default Builder setAllowed(Permission... permissions) {
            for (Permission permission : permissions)
                setState(permission, State.ALLOWED);

            return this;
        }

        default Builder setDenied(Permission... permissions) {
            for (Permission permission : permissions)
                setState(permission, State.DENIED);

            return this;
        }

        default Builder unset(Permission... permissions) {
            for (Permission permission : permissions)
                setState(permission, State.UNSET);

            return this;
        }
    }

    enum State {
        ALLOWED,

        DENIED,

        UNSET
    }

    enum TargetType {
        ROLE,

        MEMBER;

        private static @Nullable TargetType from(String value) {
            return valueOf(TargetType.class, value.toUpperCase());
        }
    }
}