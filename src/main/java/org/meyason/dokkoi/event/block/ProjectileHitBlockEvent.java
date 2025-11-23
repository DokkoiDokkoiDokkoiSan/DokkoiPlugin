package org.meyason.dokkoi.event.block;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.job.Bomber;
import org.meyason.dokkoi.job.Executor;
import org.meyason.dokkoi.job.Job;

import java.util.List;

public class ProjectileHitBlockEvent implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getHitBlock() == null){
            return;
        }
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Entity entity = event.getEntity();
        if(entity instanceof Snowball snowball) {
            ProjectileData projectileData = manager.getProjectileDataMap().get(snowball);
            if (projectileData == null) {
                return;
            }

            Player attacker = projectileData.getAttacker();
            String attackItem = projectileData.getItem();

            Job job = manager.getPlayerJobs().get(attacker);
            if (job instanceof Bomber bomber) {
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), attacker, event.getHitBlock().getLocation(), 10);
                    bomber.skill(event.getHitBlock().getLocation(), effectedPlayers);
                }else if(attackItem.equals(GameItemKeyString.ULTIMATE_SKILL)){
                    bomber.ultimate(event.getHitBlock().getLocation());
                }
            }
            manager.removeProjectileData(snowball);

        }
    }
}
