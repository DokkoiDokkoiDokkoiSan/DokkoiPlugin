package org.meyason.dokkoi.job.context;

import org.meyason.dokkoi.job.context.key.Key;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public abstract class Context<SELF extends Context<SELF>> {

    /**
     * Dataが格納されてるマップ
     * Key<?>という事になってるけど、with()のみによって媒介されるので型安全性は保たれる
     * このカプセル化は絶対に破らない（破った瞬間地獄入ります）
     */
    private final HashMap<Key<?>, Object> data = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> SELF with(Key<T> key, T value) {
        this.data.put(key, value);
        return (SELF) this;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(Key<T> key) {
        return Optional.ofNullable((T) this.data.get(key));
    }

    @SuppressWarnings("unchecked")
    public <T> T require(Key<T> key) {
        T value = (T) this.data.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing key: " + key.name());
        }
        return value;
    }

    protected Set<Key<?>> getAllKeys() {
        return this.data.keySet();
    }

    public abstract boolean isSatisfiedBy(Context<?> given);
}
