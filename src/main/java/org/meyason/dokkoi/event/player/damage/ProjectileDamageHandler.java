package org.meyason.dokkoi.event.player.damage;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.gunitem.GunItem;
import org.meyason.dokkoi.item.jobitem.Rapier;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.item.jobitem.Ultimate;
import org.meyason.dokkoi.job.*;
import org.meyason.dokkoi.util.CalculateAreaPlayers;

import java.util.List;

/**
 * 発射物によるダメージ処理を担当するクラス
 */
public class ProjectileDamageHandler {

    /**
     * 処理結果
     */
    public record HandleResult(boolean isHandled, boolean shouldContinue) {
        public static HandleResult handled() {
            return new HandleResult(true, false);
        }

        public static HandleResult continueProcessing() {
            return new HandleResult(false, true);
        }

        public static HandleResult skip() {
            return new HandleResult(true, true);
        }
    }

    /**
     * スノーボールによるダメージ処理
     */
    public static HandleResult handleSnowball(Snowball snowball, EntityDamageByEntityEvent event, 
                                               GameStatesManager gsm, Entity damagedEntity) {
        ProjectileData projectileData = gsm.getProjectileDataMap().get(snowball);
        if (projectileData == null) {
            return HandleResult.skip();
        }

        Player attacker = projectileData.getAttacker();
        DamageValidator.updateLonelyTimestamp(attacker, gsm, true);

        Job job = gsm.getPlayerJobs().get(attacker.getUniqueId());
        String attackItem = projectileData.getCustomItemName();
        if(gsm.isExistGunFromSerial(attackItem)){
            handleGunProjectile(attacker, attackItem, event, gsm, damagedEntity);
            return HandleResult.handled();
        }

        // Bomber処理
        if (job instanceof Bomber bomber) {
            handleBomberProjectile(bomber, snowball, attackItem, gsm);
            return HandleResult.handled();
        }

        // Explorer処理
        if (job instanceof Explorer explorer) {
            if (attackItem.equals(Skill.id)) {
                explorer.skill(snowball);
            }
            gsm.removeProjectileData(snowball);
            return HandleResult.handled();
        }

        // Executor処理
        if (job instanceof Executor executor) {
            executor.skill(damagedEntity);
            gsm.removeProjectileData(snowball);
            return HandleResult.handled();
        }

        // プレイヤーへのダメージ処理が必要な場合
        if (damagedEntity instanceof Player damaged) {
            gsm.addAttackedPlayer(attacker.getUniqueId());
            gsm.addDamagedPlayer(damaged.getUniqueId());
            DamageValidator.updateLonelyTimestamp(damaged, gsm, false);
            gsm.removeProjectileData(snowball);
            return HandleResult.handled();
        }

        return HandleResult.continueProcessing();
    }

    /**
     * Bomberの発射物処理
     */
    private static void handleBomberProjectile(Bomber bomber, Snowball snowball, 
                                                String attackItem, GameStatesManager gsm) {
        if (attackItem.equals(Skill.id)) {
            List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(
                    Game.getInstance(), null, snowball.getLocation(), 10);
            bomber.skill(snowball.getLocation(), effectedPlayers);
        } else if (attackItem.equals(Ultimate.id)) {
            bomber.ultimate(snowball.getLocation());
        }
        gsm.removeProjectileData(snowball);
    }

    /**
     * トライデントによるダメージ処理
     */
    public static HandleResult handleTrident(Trident trident, EntityDamageByEntityEvent event,
                                              GameStatesManager gsm) {
        ProjectileData projectileData = gsm.getProjectileDataMap().get(trident);
        if (projectileData == null) {
            return HandleResult.skip();
        }

        trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        if (!projectileData.getCustomItemName().equals(Rapier.id)) {
            return HandleResult.continueProcessing();
        }

        Player attacker = projectileData.getAttacker();
        DamageValidator.updateLonelyTimestamp(attacker, gsm, true);

        Job job = gsm.getPlayerJobs().get(attacker.getUniqueId());
        if (job instanceof IronMaiden ironMaiden) {
            event.setCancelled(true);
            Rapier rapier = ironMaiden.getRapier();
            rapier.activate(trident, trident.getLocation());
            gsm.removeProjectileData(trident);
            return HandleResult.handled();
        }

        return HandleResult.continueProcessing();
    }

    /**
     * 矢によるダメージ処理
     */
    public static HandleResult handleArrow(Arrow arrow, EntityDamageByEntityEvent event,
                                            GameStatesManager gsm, Entity damagedEntity, double damage) {
        ProjectileData projectileData = gsm.getProjectileDataMap().get(arrow);

        // 通常の矢（特殊アイテムではない）
        if (projectileData == null) {
            return handleNormalArrow(arrow, event, gsm, damagedEntity, damage);
        }

        // 特殊アイテムの矢
        return handleSpecialArrow(arrow, projectileData, gsm, damage);
    }

    /**
     * 通常の矢処理
     */
    private static HandleResult handleNormalArrow(Arrow arrow, EntityDamageByEntityEvent event,
                                                   GameStatesManager gsm, Entity damagedEntity, double damage) {
        if (!(arrow.getShooter() instanceof Entity shooterEntity)) {
            return HandleResult.skip();
        }

        // 攻撃者のLonely更新
        if (shooterEntity instanceof Player attackerPlayer) {
            DamageValidator.updateLonelyTimestamp(attackerPlayer, gsm, true);
        }

        // 被ダメージ者のLonely更新
        if (damagedEntity instanceof Player damagedPlayer) {
            DamageValidator.updateLonelyTimestamp(damagedPlayer, gsm, false);
        }

        event.setCancelled(true);

        DamageContext context = DamageContext.builder()
                .attacker(shooterEntity)
                .damaged(damagedEntity)
                .baseDamage(damage)
                .source(DamageContext.DamageSource.PROJECTILE)
                .originalEvent(event)
                .build();

        DamageCalculator.calculate(context);
        return HandleResult.handled();
    }

    /**
     * 特殊アイテムの矢処理
     */
    private static HandleResult handleSpecialArrow(Arrow arrow, ProjectileData projectileData,
                                                    GameStatesManager gsm, double damage) {
        Player attacker = projectileData.getAttacker();

        // Explorer爆発矢
        if (gsm.getPlayerJobs().get(attacker.getUniqueId()) instanceof Explorer) {
            handleExplorerArrow(arrow, attacker, gsm);
            return HandleResult.handled();
        }

        gsm.removeProjectileData(arrow);
        return HandleResult.continueProcessing();
    }

    /**
     * Explorer爆発矢処理
     */
    private static void handleExplorerArrow(Arrow arrow, Player attacker, GameStatesManager gsm) {
        Location loc = arrow.getLocation();
        arrow.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1);
        arrow.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 10.0F, 1.0F);

        List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(
                Game.getInstance(), null, loc, 3);

        gsm.addAttackedPlayer(attacker.getUniqueId());
        for (Player damaged : effectedPlayers) {
            gsm.addDamagedPlayer(damaged.getUniqueId());
        }

        gsm.removeProjectileData(arrow);
    }

    private static void handleGunProjectile(Player attacker, String gunSerial, EntityDamageByEntityEvent event,
                                            GameStatesManager gsm, Entity damagedEntity) {
        GunItem gun = gsm.getGunStatusFromSerial(gunSerial).getGun();
        double damage = gun.getBaseDamage();

        event.setCancelled(true);

        DamageContext context = DamageContext.builder()
                .attacker(attacker)
                .damaged(damagedEntity)
                .baseDamage(damage)
                .source(DamageContext.DamageSource.PROJECTILE)
                .originalEvent(event)
                .build();

        DamageCalculator.calculate(context);
        return;
    }
}
