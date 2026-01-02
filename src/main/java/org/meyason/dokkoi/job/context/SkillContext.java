package org.meyason.dokkoi.job.context;

import org.meyason.dokkoi.job.context.data.Data;
import org.meyason.dokkoi.job.context.data.LocationData;

import java.util.Set;

public final class SkillContext extends Context<SkillContext> {

    private SkillContext(){
        // prevent creating class
        super();
    }

    public static SkillContext create(){
        return new SkillContext();
    }

    @Override
    public boolean isSatisfiedBy(Context<?> given) {
        if(!(given instanceof SkillContext)) return false;
        Set<Data<?>> data = this.getAllKeys();
        Set<Data<?>> givenData = given.getAllKeys();
        return !givenData.containsAll(data);
    }
}
