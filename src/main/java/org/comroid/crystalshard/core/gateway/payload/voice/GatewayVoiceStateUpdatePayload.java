package org.comroid.crystalshard.core.gateway.payload.voice;

import org.comroid.crystalshard.DiscordBot;
import org.comroid.crystalshard.core.gateway.event.GatewayPayloadWrapper;
import org.comroid.crystalshard.core.gateway.payload.AbstractGatewayPayload;
import org.comroid.crystalshard.voice.VoiceState;
import org.comroid.uniform.node.UniObjectNode;
import org.comroid.varbind.annotation.RootBind;
import org.comroid.varbind.bind.GroupBind;
import org.comroid.varbind.bind.VarBind;

public final class GatewayVoiceStateUpdatePayload extends AbstractGatewayPayload {
    @RootBind
    public static final GroupBind<GatewayVoiceStateUpdatePayload, DiscordBot> Root
            = BaseGroup.rootGroup("gateway-voice-state-update");
    public static final VarBind<Object, UniObjectNode, DataBase, DataBase> state
            = Root.createBind("")
            .extractAsObject()
            .andConstruct(VoiceState.Root)
            .onceEach()
            .build();

    public GatewayVoiceStateUpdatePayload(GatewayPayloadWrapper gpw) {
        super(gpw);
    }
}
