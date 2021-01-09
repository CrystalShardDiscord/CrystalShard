package org.comroid.crystalshard.gateway.event.generic;

import org.comroid.api.ContextualProvider;
import org.comroid.crystalshard.gateway.event.GatewayEvent;
import org.comroid.uniform.node.UniObjectNode;
import org.comroid.varbind.bind.GroupBind;
import org.jetbrains.annotations.Nullable;

public class ReconnectEvent extends GatewayEvent {
    public static final GroupBind<ReconnectEvent> TYPE
            = BASETYPE.rootGroup("reconnect");

    public ReconnectEvent(ContextualProvider context, @Nullable UniObjectNode initialData) {
        super(context, initialData);
    }
}
