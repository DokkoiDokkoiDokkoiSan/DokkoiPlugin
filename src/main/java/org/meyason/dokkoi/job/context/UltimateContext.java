package org.meyason.dokkoi.job.context;

import org.meyason.dokkoi.job.context.data.Data;

import java.util.Set;

public final class UltimateContext extends Context<UltimateContext> {

    private UltimateContext() {
        // prevent creating class
        super();
    }

    public static UltimateContext create() {
        return new UltimateContext();
    }

    @Override
    public boolean isSatisfiedBy(Context<?> given) {
        if(!(given instanceof UltimateContext)) return false;
        Set<Data<?>> data = this.getAllKeys();
        Set<Data<?>> givenData = given.getAllKeys();
        return !givenData.containsAll(data);
    }
}
