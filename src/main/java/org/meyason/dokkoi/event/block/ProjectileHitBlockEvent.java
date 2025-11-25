package org.meyason.dokkoi.event.block;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.job.Rapier;
import org.meyason.dokkoi.job.*;

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
                    effectedPlayers.add(attacker);
                    bomber.skill(event.getHitBlock().getLocation(), effectedPlayers);
                }else if(attackItem.equals(GameItemKeyString.ULTIMATE_SKILL)){
                    bomber.ultimate(event.getHitBlock().getLocation());
                }
            }else if(job instanceof Explorer explorer) {
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    explorer.skill(snowball);
                }
            }
            manager.removeProjectileData(snowball);

        }else if(entity instanceof Trident trident){
            ProjectileData projectileData = manager.getProjectileDataMap().get(trident);
            if (projectileData == null) {
                return;
            }
            trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            if(projectileData.getItem().equals(Rapier.id)) {
                Player attacker = projectileData.getAttacker();

                Job job = manager.getPlayerJobs().get(attacker);
                if(job instanceof IronMaiden ironMaiden){
                    Rapier rapier = ironMaiden.getRapier();
                    rapier.activate(trident, trident.getLocation());
                }
            }
        }
    }
}
