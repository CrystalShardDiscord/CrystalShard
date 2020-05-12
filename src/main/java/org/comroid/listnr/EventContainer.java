package org.comroid.listnr;

public interface EventContainer<IN, D, ET extends EventType<IN, D, ? extends EP>, EP extends EventPayload<D, ? extends ET>> {
    EventType<IN, D, ? extends EP> getType();
}
