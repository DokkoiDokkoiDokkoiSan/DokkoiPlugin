package org.meyason.dokkoi.event.entity;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;

public class DespawnEvent implements Listener {

    @EventHandler
    public void onDespawn(ProjectileLaunchEvent e) {
        if (e.getEntityType() != EntityType.TRIDENT) return;

        final Trident trident = (Trident)e.getEntity();
        Game game = Game.getInstance();
        GameStatesManager manager = game.getGameStatesManager();
        if(manager.getTridentDespawnWatchDogs().containsKey(trident)){
            return;
        }

        BukkitRunnable unDespawnTridentTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(manager.getGameState() != GameState.IN_GAME){
                    this.cancel();
                    return;
                }
                trident.setTicksLived(1);
            }
        };
        unDespawnTridentTask.runTaskTimer(Dokkoi.getInstance(), 0, 80);
        manager.addTridentDespawnWatchDog(trident, unDespawnTridentTask);
    }
}
