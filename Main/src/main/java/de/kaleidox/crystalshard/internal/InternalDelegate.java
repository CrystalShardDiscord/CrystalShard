package de.kaleidox.crystalshard.internal;

import java.util.Iterator;
import java.util.ServiceLoader;

public abstract class InternalDelegate {
    public final static InternalDelegate delegate;
    
    static {
        ServiceLoader<InternalDelegate> load = ServiceLoader.load(InternalDelegate.class);
        Iterator<InternalDelegate> iterator = load.iterator();
        if (iterator.hasNext()) delegate = iterator.next();
        else throw new IllegalStateException("No implementation for " + InternalDelegate.class.getName() + " found!");
        if (iterator.hasNext()) throw new IllegalStateException("More than one implementation for " + InternalDelegate.class.getName() + " found!");
    }
    
    protected abstract <T> T makeInstance(Class<T> tClass, Object... args);
    
    public static <T> T newInstance(Class<T> tClass, Object... args) {
        return delegate.makeInstance(tClass, args);
    }
}
