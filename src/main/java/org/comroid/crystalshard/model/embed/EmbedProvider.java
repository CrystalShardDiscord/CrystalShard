package org.comroid.crystalshard.model.embed;

import org.comroid.common.Polyfill;
import org.comroid.crystalshard.CrystalShard;
import org.comroid.crystalshard.DiscordBot;
import org.comroid.uniform.node.UniValueNode.ValueType;
import org.comroid.varbind.bind.GroupBind;
import org.comroid.varbind.bind.VarBind;

import java.net.URL;

public interface EmbedProvider {
    interface Bind {
        GroupBind<EmbedThumbnail, DiscordBot> Root
                = new GroupBind<>(CrystalShard.SERIALIZATION_ADAPTER, "embed_provider");
        VarBind.TwoStage<String, URL> Url
                = Root.bind2stage("url", ValueType.STRING, Polyfill::url);
    }
}