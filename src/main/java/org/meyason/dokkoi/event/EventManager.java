package org.meyason.dokkoi.event;

import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.event.block.ProjectileHitBlockEvent;
import org.meyason.dokkoi.event.entity.DespawnEvent;
import org.meyason.dokkoi.event.entity.LaunchEvent;
import org.meyason.dokkoi.event.player.*;

public class EventManager {
    public EventManager(Dokkoi core){
        init(core);
    }

    private void init(Dokkoi core) {
        core.getServer().getPluginManager().registerEvents(new DamageEvent(), core);
//        core.getServer().getPluginManager().registerEvents(new SneakEvent(), core);
//        core.getServer().getPluginManager().registerEvents(new JumpEvent(), core);
        core.getServer().getPluginManager().registerEvents(new InteractEvent(), core);
        core.getServer().getPluginManager().registerEvents(new ProjectileHitBlockEvent(), core);
        core.getServer().getPluginManager().registerEvents(new LaunchEvent(), core);
        core.getServer().getPluginManager().registerEvents(new DespawnEvent(), core);
        core.getServer().getPluginManager().registerEvents(new LogoutEvent(), core);
        core.getServer().getPluginManager().registerEvents(new PickEvent(), core);
    }
}
