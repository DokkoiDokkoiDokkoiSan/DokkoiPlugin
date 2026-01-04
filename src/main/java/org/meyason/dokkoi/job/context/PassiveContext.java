package org.meyason.dokkoi.job.context;

import java.util.Set;

import org.meyason.dokkoi.job.context.key.Key;

public final class PassiveContext extends Context<PassiveContext>{

    private PassiveContext() {
        // prevent creating class
        super();
    }

    public static PassiveContext create() {
        return new PassiveContext();
    }

    @Override
    public boolean isSatisfiedBy(Context<?> given) {
        if(!(given instanceof PassiveContext)) return false;
        Set<Key<?>> data = this.getAllKeys();
        Set<Key<?>> givenData = given.getAllKeys();
        return givenData.containsAll(data);
    }
}
