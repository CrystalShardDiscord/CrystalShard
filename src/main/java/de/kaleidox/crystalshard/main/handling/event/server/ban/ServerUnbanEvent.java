package de.kaleidox.crystalshard.main.handling.event.server.ban;

import de.kaleidox.crystalshard.main.handling.event.server.ServerEvent;
import de.kaleidox.crystalshard.main.items.server.Server;
import de.kaleidox.crystalshard.main.items.server.interactive.Ban;
import de.kaleidox.crystalshard.main.items.user.ServerMember;

public interface ServerUnbanEvent extends ServerEvent {
    Ban getUnban();

// Override Methods
    default Server getServer() {
        return getUnban().getServer();
    }

    default ServerMember getUnbannedUser() {
        return getUnban().getUser();
    }
}
