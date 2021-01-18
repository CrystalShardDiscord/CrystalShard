package org.comroid.crystalshard.gateway.event.dispatch.channel;

import org.comroid.api.ContextualProvider;
import org.comroid.crystalshard.entity.SnowflakeCache;
import org.comroid.crystalshard.entity.Snowflake;
import org.comroid.crystalshard.entity.channel.Channel;
import org.comroid.crystalshard.gateway.event.GatewayEvent;
import org.comroid.uniform.node.UniNode;
import org.comroid.uniform.node.UniObjectNode;
import org.comroid.varbind.bind.GroupBind;
import org.comroid.varbind.bind.VarBind;
import org.jetbrains.annotations.Nullable;

public final class ChannelCreateEvent extends GatewayEvent {
    public static final GroupBind<ChannelCreateEvent> TYPE
            = BASETYPE.subGroup("channel-create", ChannelCreateEvent::new);
    public static final VarBind<ChannelCreateEvent, UniObjectNode, Channel, Channel> CHANNEL
            = TYPE.createBind("")
            .extractAsObject()
            .andResolve(Channel::resolve)
            .onceEach()
            .build();

    public ChannelCreateEvent(ContextualProvider context, @Nullable UniNode initialData) {
        super(context, initialData);
    }
}
