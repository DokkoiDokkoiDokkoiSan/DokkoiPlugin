package org.meyason.dokkoi.job.context;

import org.jspecify.annotations.Nullable;
import org.meyason.dokkoi.job.context.data.Data;
import java.util.HashMap;
import java.util.Set;

public abstract class Context<T extends Context<T>> {

    private final HashMap<Data<?>, Object> data = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <V> T with(Data<V> key, V value) {
        data.put(key, value);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <V> @Nullable V get(Data<V> key) {
        return (V) data.get(key);
    }

    protected Set<Data<?>> getAllKeys() {
        return data.keySet();
    }

    public abstract boolean isSatisfiedBy(Context<?> given);
}
