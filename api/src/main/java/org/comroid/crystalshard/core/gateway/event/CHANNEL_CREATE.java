package org.comroid.crystalshard.core.gateway.event;

// https://discordapp.com/developers/docs/topics/gateway#channel-create

import org.comroid.crystalshard.adapter.MainAPI;
import org.comroid.crystalshard.api.entity.channel.Channel;
import org.comroid.crystalshard.util.model.serialization.JSONBinding;
import org.comroid.crystalshard.util.model.serialization.JSONBindingLocation;

import com.alibaba.fastjson.JSONObject;

@MainAPI
@JSONBindingLocation(CHANNEL_CREATE.JSON.class)
public interface CHANNEL_CREATE extends GatewayEventBase {
    default Channel getChannel() {
        return getBindingValue(JSON.CHANNEL);
    }

    interface JSON {
        JSONBinding.TwoStage<JSONObject, Channel> CHANNEL = JSONBinding.rooted(Channel.class);
    }
}