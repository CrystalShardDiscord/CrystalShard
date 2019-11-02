package de.comroid.crystalshard.core.api.gateway.event.common;

import de.comroid.crystalshard.core.api.gateway.event.GatewayEvent;
import de.comroid.crystalshard.core.api.gateway.listener.common.HelloListener;
import de.comroid.crystalshard.util.annotation.InitializedBy;

public interface HelloEvent extends GatewayEvent {
    String NAME = "HELLO";

    int getHeartbeatInterval();
}
