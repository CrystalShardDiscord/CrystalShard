package de.kaleidox.crystalshard.internal.handling.event.server.ban;

import de.kaleidox.crystalshard.api.entity.server.Server;
import de.kaleidox.crystalshard.api.entity.server.interactive.Ban;
import de.kaleidox.crystalshard.api.handling.event.server.ban.ServerUnbanEvent;
import de.kaleidox.crystalshard.internal.DiscordInternal;
import de.kaleidox.crystalshard.internal.handling.event.EventBase;

public class ServerUnbanEventInternal extends EventBase implements ServerUnbanEvent {
    public ServerUnbanEventInternal(DiscordInternal discordInternal) {
        super(discordInternal);
    }

    @Override
    public Server getServer() {
        return null;
    }

    // Override Methods
    @Override
    public Ban getUnban() {
        return null;
    }
}