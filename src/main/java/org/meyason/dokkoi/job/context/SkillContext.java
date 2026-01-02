package org.meyason.dokkoi.job.context;

import org.meyason.dokkoi.job.context.key.Key;

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
        Set<Key<?>> data = this.getAllKeys();
        Set<Key<?>> givenData = given.getAllKeys();
        return !givenData.containsAll(data);
    }
}
