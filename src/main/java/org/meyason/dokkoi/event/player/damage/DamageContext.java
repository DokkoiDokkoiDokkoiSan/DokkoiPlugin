package org.meyason.dokkoi.event.player.damage;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.job.Job;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * ダメージイベントの情報を保持するコンテキストクラス
 */
public class DamageContext {
    
    public enum DamageSource {
        MELEE,           // 近接攻撃
        PROJECTILE,      // 発射物（矢、スノーボール等）
        SKILL,           // スキルによるダメージ
        MOB,             // MOBからの攻撃
        ENVIRONMENT      // 環境ダメージ（落下等）
    }

    private final Entity attacker;
    private final Entity damaged;
    private final double baseDamage;
    private final DamageSource source;
    private final EntityDamageByEntityEvent originalEvent;
    private final GameStatesManager gameStatesManager;
    
    @Nullable
    private final ProjectileData projectileData;
    
    private double calculatedDamage;
    private boolean cancelled = false;
    private String cancelReason = null;

    private DamageContext(Builder builder) {
        this.attacker = builder.attacker;
        this.damaged = builder.damaged;
        this.baseDamage = builder.baseDamage;
        this.source = builder.source;
        this.originalEvent = builder.originalEvent;
        this.projectileData = builder.projectileData;
        this.gameStatesManager = Game.getInstance().getGameStatesManager();
        this.calculatedDamage = builder.baseDamage;
    }

    // ========== Getters ==========
    
    public Entity getAttacker() {
        return attacker;
    }

    public Entity getDamaged() {
        return damaged;
    }

    @Nullable
    public Player getAttackerAsPlayer() {
        return attacker instanceof Player p ? p : null;
    }

    @Nullable
    public Player getDamagedAsPlayer() {
        return damaged instanceof Player p ? p : null;
    }

    @Nullable
    public UUID getAttackerUUID() {
        return attacker != null ? attacker.getUniqueId() : null;
    }

    @Nullable
    public UUID getDamagedUUID() {
        return damaged != null ? damaged.getUniqueId() : null;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public double getCalculatedDamage() {
        return calculatedDamage;
    }

    public void setCalculatedDamage(double damage) {
        this.calculatedDamage = damage;
    }

    public void multiplyDamage(double multiplier) {
        this.calculatedDamage *= multiplier;
    }

    public void addDamage(double amount) {
        this.calculatedDamage += amount;
    }

    public DamageSource getSource() {
        return source;
    }

    public EntityDamageByEntityEvent getOriginalEvent() {
        return originalEvent;
    }

    @Nullable
    public ProjectileData getProjectileData() {
        return projectileData;
    }

    public GameStatesManager getGameStatesManager() {
        return gameStatesManager;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel(String reason) {
        this.cancelled = true;
        this.cancelReason = reason;
    }

    @Nullable
    public String getCancelReason() {
        return cancelReason;
    }

    // ========== Utility Methods ==========

    public boolean isPlayerToPlayer() {
        return attacker instanceof Player && damaged instanceof Player;
    }

    public boolean isPlayerToMob() {
        return attacker instanceof Player && !(damaged instanceof Player);
    }

    public boolean isMobToPlayer() {
        return !(attacker instanceof Player) && damaged instanceof Player;
    }

    public boolean isProjectileAttack() {
        return source == DamageSource.PROJECTILE;
    }

    public boolean isSkillAttack() {
        return source == DamageSource.SKILL;
    }

    public boolean isMeleeAttack() {
        return source == DamageSource.MELEE;
    }

    @Nullable
    public Job getAttackerJob() {
        if (attacker instanceof Player) {
            return gameStatesManager.getPlayerJobs().get(attacker.getUniqueId());
        }
        return null;
    }

    @Nullable
    public Job getDamagedJob() {
        if (damaged instanceof Player) {
            return gameStatesManager.getPlayerJobs().get(damaged.getUniqueId());
        }
        return null;
    }

    // ========== Builder ==========
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Entity attacker;
        private Entity damaged;
        private double baseDamage;
        private DamageSource source = DamageSource.MELEE;
        private EntityDamageByEntityEvent originalEvent;
        private ProjectileData projectileData;

        public Builder attacker(Entity attacker) {
            this.attacker = attacker;
            return this;
        }

        public Builder damaged(Entity damaged) {
            this.damaged = damaged;
            return this;
        }

        public Builder baseDamage(double damage) {
            this.baseDamage = damage;
            return this;
        }

        public Builder source(DamageSource source) {
            this.source = source;
            return this;
        }

        public Builder originalEvent(EntityDamageByEntityEvent event) {
            this.originalEvent = event;
            return this;
        }

        public Builder projectileData(ProjectileData data) {
            this.projectileData = data;
            return this;
        }

        public DamageContext build() {
            return new DamageContext(this);
        }
    }
}
