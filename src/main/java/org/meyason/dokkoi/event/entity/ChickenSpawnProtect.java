package org.meyason.dokkoi.event.entity;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class ChickenSpawnProtect implements Listener {

    @EventHandler
    public void onChickenSpawn(ThrownEggHatchEvent event) {
        event.setHatching(false);
    }
}

