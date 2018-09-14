package de.kaleidox.crystalshard.main.items.permission;

import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.crystalshard.main.items.server.Server;

import java.util.Map;
import java.util.stream.Stream;

public interface PermissionOverride extends Map<Permission, OverrideState> {
    Discord getDiscord();

    Server getServer();

    Type getOverrideType();

    PermissionOverwritable getParent();

    PermissionOverride addOverride(Permission permission, OverrideState state);

    PermissionOverride removeOverride(Permission permission);

    PermissionList getAllowed();

    PermissionList getDenied();

    enum Type {
        UNKNOWN(""),

        ROLE("role"),

        USER("member");

        private final String key;

        Type(String key) {
            this.key = key;
        }

        public static Type getByKey(String key) {
            return Stream.of(values())
                    .filter(type -> type.key.equalsIgnoreCase(key))
                    .findAny()
                    .orElse(UNKNOWN);
        }

        public String getKey() {
            return key;
        }
    }
}

