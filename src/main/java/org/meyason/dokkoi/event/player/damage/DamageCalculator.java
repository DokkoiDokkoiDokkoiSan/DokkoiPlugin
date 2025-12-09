package org.meyason.dokkoi.event.player.damage;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.JobList;
import org.meyason.dokkoi.entity.Skeleton;
import org.meyason.dokkoi.event.player.DeathEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.goal.SkeletonSlayer;
import org.meyason.dokkoi.item.jobitem.SummonersBrave;
import org.meyason.dokkoi.job.Prayer;
import org.meyason.dokkoi.job.Summoner;

import java.util.Objects;

/**
 * ダメージ計算を担当するクラス
 */
public class DamageCalculator {

    private static final NamespacedKey ITEM_KEY = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
    private static final NamespacedKey ENEMY_KEY = new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.ENEMY);

    /**
     * ダメージを計算して適用する
     */
    public static void calculate(DamageContext context) {
        if (context.getAttacker() == null || context.getDamaged() == null) {
            return;
        }

        if (context.isPlayerToPlayer()) {
            calculatePlayerToPlayer(context);
        } else if (context.isPlayerToMob()) {
            calculatePlayerToMob(context);
        } else if (context.isMobToPlayer()) {
            calculateMobToPlayer(context);
        }
    }

    /**
     * スキルによるダメージを計算して適用する（外部からの呼び出し用）
     */
    public static void calculateSkillDamage(Entity attacker, Entity damaged, double damage) {
        if (attacker == null || damaged == null) {
            return;
        }

        DamageContext context = DamageContext.builder()
                .attacker(attacker)
                .damaged(damaged)
                .baseDamage(damage)
                .source(DamageContext.DamageSource.SKILL)
                .build();

        calculateSkillDamageInternal(context);
    }

    /**
     * スキルダメージの内部処理
     */
    private static void calculateSkillDamageInternal(DamageContext context) {
        if (context.isPlayerToPlayer()) {
            calculateSkillDamagePlayerToPlayer(context);
        } else if (context.isPlayerToMob()) {
            calculatePlayerToMob(context);
        }
    }

    /**
     * スキルによるプレイヤー → プレイヤーのダメージ計算
     */
    private static void calculateSkillDamagePlayerToPlayer(DamageContext context) {
        Player attacker = context.getAttackerAsPlayer();
        Player damaged = context.getDamagedAsPlayer();
        GameStatesManager gsm = context.getGameStatesManager();

        if (attacker == null || damaged == null) return;

        // 保護時間チェック
        if (Game.getInstance().getNowTime() > 500) {
            attacker.sendActionBar(Component.text("§c保護システムに攻撃が無力化された。"));
            return;
        }

        // Prayerの最強のたまたまチェック
        if (context.getDamagedJob() instanceof Prayer prayer) {
            if (prayer.getHasStrongestStrongestBall()) {
                damaged.sendActionBar(Component.text("§aもっと最強のたまたま§bが攻撃を許さない！"));
                return;
            }
        }

        double damage = context.getBaseDamage();

        // ダメージカット適用
        damage = applyDamageCut(damage, damaged, gsm);

        if (damage < 0) return;

        // ダメージ適用
        applyDamageToPlayer(attacker, damaged, damage);
    }

    /**
     * プレイヤー → プレイヤーのダメージ計算
     */
    private static void calculatePlayerToPlayer(DamageContext context) {
        Player attacker = context.getAttackerAsPlayer();
        Player damaged = context.getDamagedAsPlayer();
        GameStatesManager gsm = context.getGameStatesManager();

        if (attacker == null || damaged == null) return;

        // 保護時間チェック
        if (Game.getInstance().getNowTime() > 500) {
            attacker.sendActionBar(Component.text("§c保護システムに攻撃が無力化された。"));
            return;
        }

        // Prayerの最強のたまたまチェック
        if (context.getDamagedJob() instanceof Prayer prayer) {
            if (prayer.getHasStrongestStrongestBall()) {
                damaged.sendActionBar(Component.text("§aもっと最強のたまたま§bが攻撃を許さない！"));
                return;
            }
        }

        // 内藤特殊処理
        if (!handleNaitoInteraction(context, attacker, damaged, gsm)) {
            return;
        }

        // ダメージ計算
        double damage = context.getBaseDamage();

        // 近接攻撃の場合のみ追加ダメージを適用
        if (context.isMeleeAttack()) {
            damage = applyMeleeDamageModifiers(damage, attacker, damaged, gsm);
        }

        // ゴールによるダメージ倍率
        damage *= gsm.getPlayerGoals().get(damaged.getUniqueId()).getDamageMultiplier();

        // Executor特殊処理
        if (gsm.getKillerList().containsKey(attacker.getUniqueId()) 
                && gsm.getPlayerJobs().get(damaged.getUniqueId()).equals(JobList.EXECUTOR)) {
            damage /= 2.0;
        }

        // ダメージカット適用
        damage = applyDamageCut(damage, damaged, gsm);

        if (damage < 0) return;

        // ダメージ適用
        applyDamageToPlayer(attacker, damaged, damage);
    }

    /**
     * 近接攻撃のダメージ補正を適用
     */
    private static double applyMeleeDamageModifiers(double damage, Player attacker, Player damaged, GameStatesManager gsm) {
        double additionalDamage = gsm.getAdditionalDamage().get(attacker.getUniqueId());
        if (additionalDamage <= -300) {
            return 0.0;
        }
        return damage + additionalDamage;
    }

    /**
     * 内藤との特殊インタラクションを処理
     * @return true: 処理続行、false: 処理中断
     */
    private static boolean handleNaitoInteraction(DamageContext context, Player attacker, Player damaged, GameStatesManager gsm) {
        // 内藤がサモナーを攻撃できない
        if (gsm.isNaito(attacker.getUniqueId())) {
            if (context.getDamagedJob() instanceof Summoner) {
                attacker.sendActionBar(Component.text("§c内藤はマスターに攻撃できない！"));
                return false;
            }
        }

        // サモナーズブレイブで内藤を即死させる
        if (gsm.isNaito(damaged.getUniqueId())) {
            ItemStack item = attacker.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if (container.has(ITEM_KEY, PersistentDataType.STRING)) {
                    if (Objects.equals(container.get(ITEM_KEY, PersistentDataType.STRING), SummonersBrave.id)) {
                        DeathEvent.kill(attacker, damaged);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * プレイヤー → MOBのダメージ計算
     */
    private static void calculatePlayerToMob(DamageContext context) {
        Entity damaged = context.getDamaged();
        GameStatesManager gsm = context.getGameStatesManager();
        double damage = context.getBaseDamage();
        Player attacker = context.getAttackerAsPlayer();

        if (damaged instanceof org.bukkit.entity.Skeleton bukkitSkeleton) {
            handleSkeletonDamage(attacker, bukkitSkeleton, damage, gsm);
        }
    }

    /**
     * MOB → プレイヤーのダメージ計算
     */
    private static void calculateMobToPlayer(DamageContext context) {
        Player damaged = context.getDamagedAsPlayer();
        GameStatesManager gsm = context.getGameStatesManager();
        Entity attackedMob = context.getAttacker();

        if (damaged == null) return;

//        // 保護時間チェック
//        if (Game.getInstance().getNowTime() > 500) {
//            return;
//        }

        // Prayerの最強のたまたまチェック
        if (context.getDamagedJob() instanceof Prayer prayer) {
            if (prayer.getHasStrongestStrongestBall()) {
                damaged.sendActionBar(Component.text("§aもっと最強のたまたま§bが攻撃を許さない！"));
                return;
            }
        }

        String enemyId = attackedMob.getPersistentDataContainer().get(ENEMY_KEY, PersistentDataType.STRING);
        if (enemyId != null){
            if(gsm.getSpawnedEntitiesFromUUID(enemyId) instanceof Skeleton skeleton) {
                // 疑似的にノックバック
                skeleton.knockback(attackedMob, damaged);
            }
        }


        double damage = context.getBaseDamage();

        // ダメージカット適用
        damage = applyDamageCut(damage, damaged, gsm);

        if (damage < 0) return;

        // ダメージ適用
        applyDamageToPlayer(null, damaged, damage);
    }

    /**
     * スケルトンへのダメージ処理
     */
    private static void handleSkeletonDamage(Player attacker, org.bukkit.entity.Skeleton bukkitSkeleton, double damage, GameStatesManager gsm) {
        String enemyId = bukkitSkeleton.getPersistentDataContainer().get(ENEMY_KEY, PersistentDataType.STRING);
        if (enemyId == null) return;

        // 死亡処理
        if (bukkitSkeleton.getHealth() - damage <= 0) {
            if (gsm.getSpawnedEntitiesFromUUID(enemyId) instanceof Skeleton skeleton) {
                skeleton.kill(bukkitSkeleton, enemyId);
                if(gsm.getPlayerGoals().get(attacker.getUniqueId()) instanceof SkeletonSlayer slayer){
                    slayer.incrementSkeletonsKilled();
                }
            }
        }
    }

    /**
     * ダメージカットを適用
     */
    private static double applyDamageCut(double damage, Player damaged, GameStatesManager gsm) {
        int damageCutPercent = gsm.getDamageCutPercent().get(damaged.getUniqueId());
        return damage * (100 - damageCutPercent) / 100.0;
    }

    /**
     * プレイヤーにダメージを適用
     */
    private static void applyDamageToPlayer(Player attacker, Player damaged, double damage) {
        double afterHealth = damaged.getHealth() - damage;
        if (afterHealth <= 0) {
            DeathEvent.kill(attacker, damaged);
        } else {
            damaged.damage(damage);
        }
    }
}
