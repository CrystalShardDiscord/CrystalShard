package de.kaleidox.crystalshard.internal.event;

import de.kaleidox.crystalshard.internal.DiscordInternal;
import de.kaleidox.crystalshard.main.Discord;
import de.kaleidox.crystalshard.main.handling.listener.Listener;
import de.kaleidox.crystalshard.main.handling.listener.ListenerManager;

import java.util.concurrent.TimeUnit;

public class MultiListenerManagerInternal<T extends Listener> implements ListenerManager<T> {
    private MultiListenerManagerInternal(DiscordInternal discord, T listener) {
        // todo
    }

    @Override
    public Discord getDiscord() {
        return null;
    }

    @Override
    public T getListener() {
        return null;
    }

    @Override
    public ListenerManager<T> detachNow() {
        return null;
    }

    @Override
    public ListenerManager<T> detachIn(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public ListenerManager<T> onDetach(Runnable runnable) {
        return null;
    }
}