package org.meyason.dokkoi.job.context.data;

import org.bukkit.entity.Entity;

public final class EntityData implements Data<Entity> {

    public static final EntityData KEY = new EntityData();

    private EntityData(){}
}
