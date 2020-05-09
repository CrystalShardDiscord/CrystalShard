package org.comroid.listnr;

import org.comroid.common.Polyfill;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

import java.util.function.Predicate;

@FunctionalInterface
public interface TypeHandler<ET extends EventType<?, ?, EP>, EP extends EventPayload<ET>> extends Predicate<EP> {
    @OverrideOnly
    default Class<EP> getEventPayloadType() {
        return Polyfill.uncheckedCast(EventPayload.class);
    }

    @OverrideOnly
    default ET getMasterEventType() {
        throw new AbstractMethodError("Method must be overridden as a default interface method");
    }

    @Override
    default boolean test(EP payload) {
        return getEventPayloadType().isInstance(payload);
    }

    void handle(EP payload);
}