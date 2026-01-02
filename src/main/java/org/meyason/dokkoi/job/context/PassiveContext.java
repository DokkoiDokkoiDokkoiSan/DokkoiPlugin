package org.meyason.dokkoi.job.context;

import org.meyason.dokkoi.job.context.data.Data;

import java.util.Set;

import org.meyason.dokkoi.job.context.Context;
import org.meyason.dokkoi.job.context.data.Data;

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
        Set<Data<?>> data = this.getAllKeys();
        Set<Data<?>> givenData = given.getAllKeys();
        return givenData.containsAll(data);
    }
}
