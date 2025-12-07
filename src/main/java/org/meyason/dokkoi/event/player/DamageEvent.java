package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.meyason.dokkoi.event.player.damage.DamageCalculator;
import org.meyason.dokkoi.event.player.damage.DamageContext;
import org.meyason.dokkoi.event.player.damage.DamageValidator;
import org.meyason.dokkoi.event.player.damage.ProjectileDamageHandler;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;

/**
 * ダメージイベントのリスナー
 * 
 * 責務:
 * - イベントの振り分けと事前検証
 * - 各種ダメージ処理クラスへの委譲
 * 
 * @see DamageCalculator ダメージ計算
 * @see DamageValidator 事前検証
 * @see ProjectileDamageHandler 発射物処理
 */
public class DamageEvent implements Listener {

    // ================================
    // プレイヤー → プレイヤー（近接攻撃）
    // ================================
    @EventHandler
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player damaged)) return;

        GameStatesManager gsm = Game.getInstance().getGameStatesManager();

        // 事前検証
        if (!DamageValidator.isGameInProgress()) {
            event.setCancelled(true);
            return;
        }

        if (DamageValidator.hasNoDamageTicks(damaged)) {
            event.setCancelled(true);
            return;
        }

        // 最強のたまたまチェック（攻撃者・被ダメージ者両方）
        DamageValidator.ValidationResult attackerCheck = DamageValidator.checkStrongestBall(attacker, gsm);
        if (attackerCheck.shouldCancel()) {
            event.setCancelled(true);
            attacker.sendActionBar(Component.text(attackerCheck.message()));
            return;
        }

        DamageValidator.ValidationResult damagedCheck = DamageValidator.checkStrongestBall(damaged, gsm);
        if (damagedCheck.shouldCancel()) {
            event.setCancelled(true);
            damaged.sendActionBar(Component.text(damagedCheck.message()));
            return;
        }

        // 戦闘参加者を登録
        DamageValidator.registerCombatants(attacker, damaged, gsm);

        // 一度だけダメージ無効化チェック
        if (DamageValidator.checkDisableDamageOnce(damaged, gsm)) {
            event.setCancelled(true);
            return;
        }

        // 最強のたまたまによるダメージ軽減
        if (DamageValidator.checkStrongestBallDamageReduction(damaged, gsm)) {
            event.setCancelled(true);
            return;
        }

        // ダメージ計算
        event.setCancelled(true);
        DamageContext context = DamageContext.builder()
                .attacker(attacker)
                .damaged(damaged)
                .baseDamage(event.getFinalDamage())
                .source(DamageContext.DamageSource.MELEE)
                .originalEvent(event)
                .build();

        DamageCalculator.calculate(context);
    }

    // ================================
    // 発射物 → エンティティ
    // ================================
    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        // プレイヤー直接攻撃は別ハンドラで処理
        if (event.getDamager() instanceof Player) return;

        // 発射物以外は別ハンドラで処理
        if (!(event.getDamager() instanceof Projectile)) return;

        GameStatesManager gsm = Game.getInstance().getGameStatesManager();
        Entity damagedEntity = event.getEntity();

        if (damagedEntity.isDead()) return;
        if (!(damagedEntity instanceof LivingEntity)) return;

        double damage = event.getFinalDamage();

        // プレイヤーが被ダメージの場合の事前チェック
        if (damagedEntity instanceof Player damagedPlayer) {
            if (DamageValidator.checkDisableDamageOnce(damagedPlayer, gsm)) {
                event.setCancelled(true);
                return;
            }

            DamageValidator.ValidationResult check = DamageValidator.checkStrongestBall(damagedPlayer, gsm);
            if (check.shouldCancel()) {
                event.setCancelled(true);
                damagedPlayer.sendActionBar(Component.text(check.message()));
                return;
            }
        }

        // 発射物の種類別処理
        ProjectileDamageHandler.HandleResult result;

        if (event.getDamager() instanceof Snowball snowball) {
            result = ProjectileDamageHandler.handleSnowball(snowball, event, gsm, damagedEntity);
        } else if (event.getDamager() instanceof Trident trident) {
            result = ProjectileDamageHandler.handleTrident(trident, event, gsm);
        } else if (event.getDamager() instanceof Arrow arrow) {
            result = ProjectileDamageHandler.handleArrow(arrow, event, gsm, damagedEntity, damage);
        } else {
            return;
        }

        if (result.isHandled()) {
            return;
        }
    }

    // ================================
    // プレイヤー → MOB
    // ================================
    @EventHandler
    public void onPlayerAttackMob(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if (damaged instanceof Player) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (damaged.isDead()) return;

        GameStatesManager gsm = Game.getInstance().getGameStatesManager();

        if (!DamageValidator.isGameInProgress()) {
            event.setCancelled(true);
            return;
        }

        DamageValidator.updateLonelyTimestamp(attacker, gsm, true);

        DamageContext context = DamageContext.builder()
                .attacker(attacker)
                .damaged(damaged)
                .baseDamage(event.getFinalDamage())
                .source(DamageContext.DamageSource.MELEE)
                .originalEvent(event)
                .build();

        DamageCalculator.calculate(context);
    }

    // ================================
    // MOB → プレイヤー
    // ================================
    @EventHandler
    public void onMobAttackPlayer(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (attacker instanceof Player) return;
        if (attacker instanceof Projectile) return;
        if (!(event.getEntity() instanceof Player damaged)) return;

        GameStatesManager gsm = Game.getInstance().getGameStatesManager();

        if (!DamageValidator.isGameInProgress()) {
            event.setCancelled(true);
            return;
        }

        if (DamageValidator.hasNoDamageTicks(damaged)) {
            event.setCancelled(true);
            return;
        }

        DamageValidator.ValidationResult check = DamageValidator.checkStrongestBall(damaged, gsm);
        if (check.shouldCancel()) {
            event.setCancelled(true);
            damaged.sendActionBar(Component.text(check.message()));
            return;
        }

        gsm.addDamagedPlayer(damaged.getUniqueId());
        DamageValidator.updateLonelyTimestamp(damaged, gsm, false);

        if (DamageValidator.checkDisableDamageOnce(damaged, gsm)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        DamageContext context = DamageContext.builder()
                .attacker(attacker)
                .damaged(damaged)
                .baseDamage(event.getFinalDamage())
                .source(DamageContext.DamageSource.MOB)
                .originalEvent(event)
                .build();

        DamageCalculator.calculate(context);
    }

    // ================================
    // 環境ダメージ（落下、奈落）
    // ================================
    @EventHandler
    public void onEnvironmentDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        if (!DamageValidator.isGameInProgress()) {
            event.setCancelled(true);
            return;
        }

        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.FALL || 
            cause == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
        }
    }
}
