package org.meyason.dokkoi.event.player;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.JobList;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.job.Bomber;
import org.meyason.dokkoi.job.Executor;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.job.Lonely;

import java.util.List;

public class DamageEvent implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();
        if(!(attacker instanceof Player attackedPlayer) || !(damaged instanceof Player damagedPlayer)) return;

        if(damagedPlayer.getNoDamageTicks() >= 10){
            event.setCancelled(true);
            return;
        }
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        if(gameStatesManager.getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }

        gameStatesManager.addAttackedPlayer(attackedPlayer);
        gameStatesManager.addDamagedPlayer(damagedPlayer);

        if(gameStatesManager.getPlayerJobs().get(attackedPlayer) instanceof Lonely lonely){
            lonely.lastAttackedTime = System.currentTimeMillis();
        }else if(gameStatesManager.getPlayerJobs().get(damagedPlayer) instanceof Lonely lonely){
            lonely.lastDamagedTime = System.currentTimeMillis();
        }

        double damage = event.getFinalDamage() * gameStatesManager.getPlayerGoals().get(damaged).getDamageMultiplier();
        if(gameStatesManager.getKillerList().containsKey(attacker) && gameStatesManager.getPlayerJobs().get(damaged).equals(JobList.EXECUTOR)) {
            damage /= 2.0;
        }

        double afterHealth = damagedPlayer.getHealth() - event.getFinalDamage();
        // 死亡処理
        if(afterHealth < 0) {
            event.setCancelled(true);

            DeathEvent.kill(damagedPlayer, attackedPlayer);
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
            if (job instanceof Bomber bomber) {
                String attackItem = projectileData.getItem();
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), attacker, snowball.getLocation(), 10);
                    bomber.skill(snowball.getLocation(), effectedPlayers);
                }else if(attackItem.equals(GameItemKeyString.ULTIMATE_SKILL)){
                    bomber.ultimate(snowball.getLocation());
                }
                return;
            }

            if(livingEntity instanceof Player damaged) {

                manager.addAttackedPlayer(attacker);
                manager.addDamagedPlayer(damaged);

                if (job instanceof Executor executor) {
                    executor.skill(damaged);
                }
                manager.removeProjectileData(snowball);
            }
        }
    }
}
