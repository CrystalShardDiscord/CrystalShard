package de.kaleidox.crystalshard.api.entity.permission;

import de.kaleidox.crystalshard.api.entity.DiscordItem;
import de.kaleidox.crystalshard.api.entity.server.Server;

public interface PermissionOverwritable extends DiscordItem {
    Server getServer();
}
