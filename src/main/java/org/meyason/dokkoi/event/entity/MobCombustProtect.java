package org.meyason.dokkoi.event.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;

public class MobCombustProtect implements Listener {

    @EventHandler
    public void onMobCombust(EntityCombustEvent event) {
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        if(manager.getGameState() != GameState.IN_GAME){
            return;
        }

        NamespacedKey enemyKey = new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.ENEMY);
        Entity entity = event.getEntity();
        if(entity.getPersistentDataContainer().has(enemyKey)){
            event.setCancelled(true);
        }
    }
}
