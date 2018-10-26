package de.kaleidox.crystalshard.internal.handling.event.server.generic;

import de.kaleidox.crystalshard.internal.DiscordInternal;
import de.kaleidox.crystalshard.internal.handling.event.EventBase;
import de.kaleidox.crystalshard.main.handling.editevent.EditTrait;
import de.kaleidox.crystalshard.main.handling.event.server.generic.ServerEditEvent;
import de.kaleidox.crystalshard.main.items.server.Server;
import de.kaleidox.crystalshard.util.annotations.NotContainNull;
import de.kaleidox.crystalshard.util.annotations.NotNull;

import java.util.Set;

public class ServerEditEventInternal extends EventBase implements ServerEditEvent {
    private final Server                 server;
    private final Set<EditTrait<Server>> editTraits;
    
    public ServerEditEventInternal(DiscordInternal discordInternal, @NotNull Server server, @NotContainNull Set<EditTrait<Server>> editTraits) {
        super(discordInternal);
        this.server = server;
        this.editTraits = editTraits;
    }
    
    // Override Methods
    @Override
    public Set<EditTrait<Server>> getEditTraits() {
        return editTraits;
    }
    
    @Override
    public Server getServer() {
        return server;
    }
}