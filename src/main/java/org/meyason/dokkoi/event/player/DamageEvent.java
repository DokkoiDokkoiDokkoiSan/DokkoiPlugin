package org.meyason.dokkoi.event.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.EntityID;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.JobList;
import org.meyason.dokkoi.entity.Comedian;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.job.Rapier;
import org.meyason.dokkoi.job.*;

import java.util.List;

public class DamageEvent implements Listener {

    // プレイヤーからプレイヤーへのダメージ
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

        double damage = event.getFinalDamage();
        double additionalDamage = gameStatesManager.getAdditionalDamage().get(attackedPlayer);
        if(additionalDamage <= -300){
            damage = 1.0;
        }else{
            damage += additionalDamage;
        }

        damage *= gameStatesManager.getPlayerGoals().get(damaged).getDamageMultiplier();
        if(gameStatesManager.getKillerList().containsKey(attacker) && gameStatesManager.getPlayerJobs().get(damaged).equals(JobList.EXECUTOR)) {
            damage /= 2.0;
        }

        double afterHealth = damagedPlayer.getHealth() - damage;
        // 死亡処理
        if(afterHealth <= 0) {
            event.setCancelled(true);

            DeathEvent.kill(attackedPlayer, damagedPlayer);
            return;
        }
    }

    // エンティティからプレイヤーへのダメージ
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        if(event.getDamager() instanceof Player) return;

        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        Entity entity = event.getEntity();
        if (event.getEntity().isDead()) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        if(event.getDamager() instanceof Snowball snowball) {
            ProjectileData projectileData = gameStatesManager.getProjectileDataMap().get(snowball);
            if (projectileData == null) {
                return;
            }

            Player attacker = projectileData.getAttacker();
            if(gameStatesManager.getPlayerJobs().get(attacker) instanceof Lonely lonely){
                lonely.lastDamagedTime = System.currentTimeMillis();
            }

            Job job = gameStatesManager.getPlayerJobs().get(attacker);
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

                gameStatesManager.addAttackedPlayer(attacker);
                gameStatesManager.addDamagedPlayer(damaged);

                if (job instanceof Executor executor) {
                    executor.skill(damaged);
                }
                gameStatesManager.removeProjectileData(snowball);
            }
        }else if(event.getDamager() instanceof Trident trident) {
            ProjectileData projectileData = gameStatesManager.getProjectileDataMap().get(trident);
            if (projectileData == null) {
                return;
            }
            trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            if(projectileData.getItem().equals(Rapier.id)) {
                Player attacker = projectileData.getAttacker();
                if(gameStatesManager.getPlayerJobs().get(attacker) instanceof Lonely lonely){
                    lonely.lastDamagedTime = System.currentTimeMillis();
                }

                Job job = gameStatesManager.getPlayerJobs().get(attacker);
                if(job instanceof IronMaiden ironMaiden){
                    Rapier rapier = ironMaiden.getRapier();
                    rapier.activate(trident, trident.getLocation());
                }
            }
        }

    }

    //　プレイヤーからエンティティへのダメージ
    @EventHandler
    public static void onEntityDamage(EntityDamageByEntityEvent event){
        Entity damaged = event.getEntity();
        if(damaged instanceof Player){return;}
        if(!(event.getDamager() instanceof Player attacker)){return;}
        if(damaged.isDead()){
            return;
        }
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        if(gameStatesManager.getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }

        if(gameStatesManager.getPlayerJobs().get(attacker) instanceof Lonely lonely){
            lonely.lastAttackedTime = System.currentTimeMillis();
        }

        if(damaged instanceof Villager villager){
            for(EntityID entityID : EntityID.values()){
                if(villager.getPersistentDataContainer().has(new NamespacedKey(Dokkoi.getInstance(), entityID.getId()), PersistentDataType.STRING)){
                    if(Comedian.isComedianByID(entityID.getId())){
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}
