package de.comroid.crystalshard.core.api.gateway.event.common;

// https://discordapp.com/developers/docs/topics/gateway#resumed

import de.comroid.crystalshard.core.api.gateway.event.GatewayEvent;
import de.comroid.crystalshard.core.api.gateway.listener.common.ResumedListener;
import de.comroid.crystalshard.util.annotation.InitializedBy;

public interface ResumedEvent extends GatewayEvent {
    String NAME = "RESUMED";
}
