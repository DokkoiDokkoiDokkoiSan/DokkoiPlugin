package org.meyason.dokkoi.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.meyason.dokkoi.game.Game;

public class DeathEvent implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player deadPlayer = event.getPlayer();
        Entity killer = deadPlayer.getKiller();
        if(killer instanceof Player killerPlayer){
            Game.getInstance().getKillerList().put(killerPlayer, deadPlayer);
        }
    }
}
