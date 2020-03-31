package org.comroid.crystalshard.api.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.comroid.crystalshard.adapter.Constructor;
import org.comroid.crystalshard.core.gateway.Gateway;
import org.comroid.crystalshard.util.model.NonThrowingCloseable;

import static org.jetbrains.annotations.ApiStatus.Internal;

public interface EventHandler<E extends EventBase> {
    <X extends E> EventHandler.API<X> listenTo(Class<X> eventType);

    <X extends E> NonThrowingCloseable listenUsing(EventAdapter<X> eventAdapter);

    boolean detachHandlerIf(Class<? extends E> targetType, Predicate<Consumer<E>> filter);

    boolean detachAdapterIf(Class<? extends E> targetType, Predicate<EventAdapter<E>> filter);

    @Internal
    void submitEvent(E event);

    @Constructor({EventHandler.class, EventBase.class})
    interface API<E> {
        API<E> when(Predicate<E> filter);

        API<E> onlyFor(long time, TimeUnit unit);

        API<E> onlyFor(long times);

        CompletableFuture<E> onlyOnce();

        NonThrowingCloseable handle(Consumer<E> handler);
    }

    abstract class EventAdapter<E extends EventBase> {
        public final Collection<Class<E>> targetTypes = new ArrayList<>();

        @SafeVarargs
        protected EventAdapter(final Class<E>... targetTypes) {
            this.targetTypes.addAll(Arrays.asList(targetTypes));
        }

        public boolean accepts(Class<? extends E> event) {
            return targetTypes.stream()
                    .anyMatch(it -> it.isAssignableFrom(event));
        }

        public abstract <X extends E> void handle(X handle);
    }
}