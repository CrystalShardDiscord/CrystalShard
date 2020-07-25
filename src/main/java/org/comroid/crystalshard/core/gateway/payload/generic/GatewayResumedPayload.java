package org.comroid.crystalshard.core.gateway.payload.generic;

import org.comroid.crystalshard.DiscordBot;
import org.comroid.crystalshard.core.gateway.event.GatewayPayloadWrapper;
import org.comroid.crystalshard.core.gateway.payload.AbstractGatewayPayload;
import org.comroid.varbind.annotation.Location;
import org.comroid.varbind.annotation.RootBind;
import org.comroid.varbind.bind.GroupBind;

public final class GatewayResumedPayload extends AbstractGatewayPayload {
    @RootBind
    public static final GroupBind<GatewayResumedPayload, DiscordBot> Root
            = BaseGroup.subGroup("gateway-resume", GatewayResumedPayload.class);

    public GatewayResumedPayload(GatewayPayloadWrapper gpw) {
        super(gpw);
    }
}