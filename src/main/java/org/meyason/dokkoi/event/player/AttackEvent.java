package org.meyason.dokkoi.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;

public class AttackEvent implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();
        if(!(attacker instanceof Player) || !(damaged instanceof Player)) return;

        if(((Player) damaged).getNoDamageTicks() >= 10){
            event.setCancelled(true);
            return;
        }
        if(Game.getInstance().getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }
    }
}
