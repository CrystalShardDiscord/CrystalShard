package org.comroid.crystalshard.entity.channel;

import org.comroid.uniform.node.impl.StandardValueType;
import org.comroid.varbind.bind.GroupBind;
import org.comroid.varbind.bind.VarBind;

public interface VoiceChannel extends Channel {
    GroupBind<VoiceChannel> BASETYPE
            = Channel.BASETYPE.subGroup("voice-channel");
    VarBind<VoiceChannel, Integer, Integer, Integer> BITRATE
            = BASETYPE.createBind("bitrate")
            .extractAs(StandardValueType.INTEGER)
            .asIdentities()
            .onceEach()
            .setRequired()
            .build();
    VarBind<VoiceChannel, Integer, Integer, Integer> USER_LIMIT
            = BASETYPE.createBind("user_limit")
            .extractAs(StandardValueType.INTEGER)
            .asIdentities()
            .onceEach()
            .build();
}
