package org.meyason.dokkoi.event.player;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.JobList;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.job.Executor;
import org.meyason.dokkoi.job.Job;

public class DamageEvent implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();
        if(!(attacker instanceof Player killer) || !(damaged instanceof Player dead)) return;

        if(dead.getNoDamageTicks() >= 10){
            event.setCancelled(true);
            return;
        }
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        if(gameStatesManager.getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }

        double damage = event.getFinalDamage() * gameStatesManager.getPlayerGoals().get(damaged).getDamageMultiplier();

        if(gameStatesManager.getPlayerJobs().get(damaged).equals(JobList.EXECUTOR)) {
            damage /= 2.0;
        }

        double afterHealth = dead.getHealth() - event.getFinalDamage();
        // 死亡処理
        if(afterHealth < 0) {
            event.setCancelled(true);

            DeathEvent.kill(dead, killer);
            return;
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Entity entity = event.getEntity();
        if (event.getEntity().isDead()) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        if(event.getDamager() instanceof Snowball snowball) {
            ProjectileData projectileData = manager.getProjectileDataMap().get(snowball);
            if (projectileData == null) {
                return;
            }
            Player attacker = projectileData.getAttacker();
            Job job = manager.getPlayerJobs().get(attacker);
            if(job instanceof Executor executor) {
                if(livingEntity instanceof Player damaged) {
                    executor.skill(damaged);
                }
            }
            manager.removeProjectileData(snowball);
        }
    }
}
