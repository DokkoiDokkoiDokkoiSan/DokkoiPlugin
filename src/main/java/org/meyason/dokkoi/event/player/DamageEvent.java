package org.meyason.dokkoi.event.player;

import net.kyori.adventure.sound.Sound;
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

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.PickupRules;
import org.spongepowered.api.effect.VanishState;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.trader.Villager;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.Snowball;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.entity.projectile.arrow.Trident;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.projectile.source.ProjectileSource;

import java.util.List;
import java.util.Optional;

public class DamageEvent implements EventListener<DamageEntityEvent> {

    // イベントのメインハンドラ
    @Override
    public void handle(DamageEntityEvent event){
        Entity damaged = event.entity();
        Cause cause = event.cause();

        cause.first(Player.class).ifPresent(player -> {
            if(damaged instanceof Player damagedPlayer){
                fromPlayertoPlayerDamage(event, player, damagedPlayer);
            }else{
                fromPlayerToEntityDamage(event, player, damaged);
            }
        });

        cause.first(Projectile.class).ifPresent(projectile -> {
            fromProjectileDamage(event, projectile, damaged);
        });
    }

    // プレイヤーからプレイヤーへのダメージ
    public void fromPlayertoPlayerDamage(DamageEntityEvent event, Player attackedPlayer, Player damagedPlayer) {
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

        event.setCancelled(true);
        calculateDamage(attackedPlayer, damagedPlayer, event.finalDamage());
    }

    // Projectileからのダメージ
    public void fromProjectileDamage(DamageEntityEvent event, Projectile projectile, Entity damagedEntity) {
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        if (projectile.isRemoved()) {
            return;
        }
        if (!(damagedEntity instanceof Living livingEntity)) {
            return;
        }

        Player attackedPlayer = null;
        Player damagedPlayer = null;
        double damage = event.finalDamage();

        Optional<ProjectileSource> shooterOpt = projectile.get(Keys.SHOOTER);
        if (shooterOpt.isEmpty()) {
            return;
        }

        // だいたいのスキル系投擲物はsnowball
        if(projectile instanceof Snowball snowball) {
            ProjectileData projectileData = gameStatesManager.getProjectileDataMap().get(snowball);
            if (projectileData == null) {
                return;
            }

            attackedPlayer = projectileData.getAttacker();
            if(gameStatesManager.getPlayerJobs().get(attackedPlayer) instanceof Lonely lonely){
                lonely.lastAttackedTime = System.currentTimeMillis();
            }

            Job job = gameStatesManager.getPlayerJobs().get(attackedPlayer);

            // 当たったエンティティがプレイヤーじゃなくてもいい場合はこっち
            if (job instanceof Bomber bomber) {
                String attackItem = projectileData.getItem();
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), attackedPlayer, snowball.getLocation(), 10);
                    bomber.skill(snowball.location(), effectedPlayers);
                }else if(attackItem.equals(GameItemKeyString.ULTIMATE_SKILL)){
                    bomber.ultimate(snowball.location());
                }
                gameStatesManager.removeProjectileData(snowball);
                return;
            }else if(job instanceof Explorer explorer) {
                String attackItem = projectileData.getItem();
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    explorer.skill(snowball);
                }
                gameStatesManager.removeProjectileData(snowball);
                return;
            }

            // 当たったエンティティがプレイヤーに限定する効果はこっち
            if(livingEntity instanceof Player damaged) {

                damagedPlayer = damaged;

                gameStatesManager.addAttackedPlayer(attackedPlayer);
                gameStatesManager.addDamagedPlayer(damagedPlayer);

                if(gameStatesManager.getPlayerJobs().get(damagedPlayer) instanceof Lonely lonely){
                    lonely.lastDamagedTime = System.currentTimeMillis();
                }

                if (job instanceof Executor executor) {
                    executor.skill(damagedPlayer);
                }
                gameStatesManager.removeProjectileData(snowball);
                return;
            }

        }else if(projectile instanceof Trident trident) {
            ProjectileData projectileData = gameStatesManager.getProjectileDataMap().get(trident);
            // 特殊アイテムじゃないばあい(素のトライデント)
            if (projectileData == null) {
                return;
            }

            trident.offer(Keys.PICKUP_RULE, PickupRules.DISALLOWED.get());

            if(projectileData.getItem().equals(Rapier.id)) {
                attackedPlayer = projectileData.getAttacker();
                if(gameStatesManager.getPlayerJobs().get(attackedPlayer) instanceof Lonely lonely){
                    lonely.lastAttackedTime = System.currentTimeMillis();
                }

                Job job = gameStatesManager.getPlayerJobs().get(attackedPlayer);
                if(job instanceof IronMaiden ironMaiden){
                    event.setCancelled(true);

                    Rapier rapier = ironMaiden.getRapier();
                    rapier.activate(trident, trident.location());
                    gameStatesManager.removeProjectileData(trident);
                    return;
                }
            }

        }else if(projectile instanceof Arrow arrow) {
            ProjectileData projectileData = gameStatesManager.getProjectileDataMap().get(arrow);

            if (projectileData == null) {
                Entity shooterEntity = (Entity) shooterOpt.get();
                if (shooterEntity instanceof Player attackerPlayer) {
                    if (gameStatesManager.getPlayerJobs().get(attackerPlayer) instanceof Lonely lonely) {
                        lonely.lastAttackedTime = System.currentTimeMillis();
                    }
                }
                if(damagedEntity instanceof Player damagedP){
                    if(gameStatesManager.getPlayerJobs().get(damagedP) instanceof Lonely lonely){
                        lonely.lastDamagedTime = System.currentTimeMillis();
                    }
                }
                calculateDamage(shooterEntity, damagedEntity, damage);
                return;
            }

            attackedPlayer = projectileData.getAttacker();
            if(gameStatesManager.getPlayerJobs().get(attackedPlayer) instanceof Explorer) {
                //自分が放つ矢が着弾した位置に爆発を起こす。爆発は当たった対象に固定10ダメージを与える
                ParticleEffect explosionEffect = ParticleEffect.builder()
                        .type(ParticleTypes.EXPLOSION_EMITTER)
                        .build();
                arrow.world().spawnParticles(explosionEffect, arrow.location().position());
                Sound sound = Sound.sound(SoundTypes.ENTITY_GENERIC_EXPLODE, Sound.Source.MASTER, 10.0f, 1.0f);
                arrow.world().playSound(sound, arrow.location().position());
                List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), attackedPlayer, arrow.location(), 3);
                effectedPlayers.add(attackedPlayer);
                gameStatesManager.addAttackedPlayer(attackedPlayer);
                for (Player damaged : effectedPlayers) {
                    calculateDamage(attackedPlayer, damaged, 10.0);
                    gameStatesManager.addDamagedPlayer(damaged);
                }
                gameStatesManager.removeProjectileData(arrow);
                return;
            }
            gameStatesManager.removeProjectileData(arrow);
        }

        calculateDamage(attackedPlayer, damagedPlayer, damage);
    }

//    // エンティティからのダメージ
//    public void fromEntityDamage(DamageEntityEvent event, Entity attacker, Player damagedPlayer) {
//
//    }

    // プレイヤーからエンティティへのダメージ
    public void fromPlayerToEntityDamage(DamageEntityEvent event, Player attacker, Entity damagedEntity) {
        if(damagedEntity instanceof Player){return;}
        Optional<VanishState> vanishStateOpt = attacker.get(Keys.VANISH_STATE);
        if(vanishStateOpt.isPresent() && vanishStateOpt.get().equals(VanishState.vanished())){
            event.setCancelled(true);
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

        if(damagedEntity instanceof Villager villager){
            if(gameStatesManager.isComedian(villager)){
                event.setCancelled(true);
                return;
            }
        }
    }

    public static void calculateDamage(Entity attacker, Entity damaged, double damage){
        if(attacker == null || damaged == null) {
            return;
        }
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        if(damaged instanceof Player damagedPlayer && attacker instanceof Player attackerPlayer) {
            double additionalDamage = gameStatesManager.getAdditionalDamage().get(attackerPlayer);
            if (additionalDamage <= -300) {
                damage = 1.0;
            } else {
                damage += additionalDamage;
            }

            damage *= gameStatesManager.getPlayerGoals().get(damaged).getDamageMultiplier();
            if (gameStatesManager.getKillerList().containsKey(attackerPlayer) && gameStatesManager.getPlayerJobs().get(damaged).equals(JobList.EXECUTOR)) {
                damage /= 2.0;
            }

            int damageCutPercent = gameStatesManager.getDamageCutPercent().get(damaged);
            damage = damage * (100 - damageCutPercent) / 100.0;

            if (damage < 0) {
                return;
            }

            Optional<Double> optHealth = damagedPlayer.get(Keys.HEALTH);
            if (optHealth.isEmpty()) {
                return;
            }
            double currentHealth = optHealth.get();
            double afterHealth = currentHealth - damage;
            // 死亡処理
            if (afterHealth <= 0) {
                DeathEvent.kill(attackerPlayer, damagedPlayer);
            }else{
                damagedPlayer.offer(Keys.HEALTH, afterHealth);
            }
        }
    }
}
