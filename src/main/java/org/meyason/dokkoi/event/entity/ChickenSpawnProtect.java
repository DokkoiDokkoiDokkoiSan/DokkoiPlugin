package org.meyason.dokkoi.event.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class ChickenSpawnProtect implements Listener {

    @EventHandler
    public void onChickenSpawn(CreatureSpawnEvent event) {
        // 卵から鶏が生まれるのをブロック
        if (event.getEntityType() == EntityType.CHICKEN &&
            event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }
}

