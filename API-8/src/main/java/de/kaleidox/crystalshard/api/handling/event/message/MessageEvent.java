package de.kaleidox.crystalshard.api.handling.event.message;

import de.kaleidox.crystalshard.api.handling.event.Event;
import de.kaleidox.crystalshard.api.entity.message.Message;

public interface MessageEvent extends Event {
    default long getMessageId() {
        return getMessage().getId();
    }

    Message getMessage();

    default boolean isPrivate() {
        return getMessage().isPrivate();
    }
}
