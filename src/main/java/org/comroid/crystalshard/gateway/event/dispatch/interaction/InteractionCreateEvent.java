package org.comroid.crystalshard.gateway.event.dispatch.interaction;

import org.comroid.api.ContextualProvider;
import org.comroid.crystalshard.gateway.event.GatewayEvent;
import org.comroid.crystalshard.gateway.event.dispatch.DispatchEvent;
import org.comroid.crystalshard.gateway.event.dispatch.guild.emoji.GuildEmojisUpdateEvent;
import org.comroid.uniform.node.UniNode;
import org.comroid.varbind.annotation.RootBind;
import org.comroid.varbind.bind.GroupBind;
import org.jetbrains.annotations.Nullable;

public final class InteractionCreateEvent extends DispatchEvent {
    @RootBind
    public static final GroupBind<InteractionCreateEvent> TYPE
            = BASETYPE.subGroup("interaction-create", InteractionCreateEvent::new);

    public InteractionCreateEvent(ContextualProvider context, @Nullable UniNode initialData) {
        super(context, initialData);

        // todo Implement Interactions https://discord.com/developers/docs/interactions/slash-commands#interaction
    }
}
