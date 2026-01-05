package org.meyason.dokkoi.job.type;

import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileHitHooker {
    void onProjectileHit(ProjectileHitEvent event);
}
