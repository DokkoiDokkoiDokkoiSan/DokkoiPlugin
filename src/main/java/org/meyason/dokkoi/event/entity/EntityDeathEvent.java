package org.meyason.dokkoi.event.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.game.Game;

public class EntityDeathEvent implements Listener {

    @EventHandler
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        Entity entity = event.getEntity();
        NamespacedKey enemyKey = new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.ENEMY);

        String enemyId = entity.getPersistentDataContainer().get(enemyKey, org.bukkit.persistence.PersistentDataType.STRING);
        if (enemyId != null) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }
    }
}

