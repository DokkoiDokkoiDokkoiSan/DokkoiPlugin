package org.meyason.dokkoi.event.player.damage;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.battleitem.ArcherArmor;
import org.meyason.dokkoi.item.jobitem.gacha.StrongestBall;
import org.meyason.dokkoi.job.Lonely;
import org.meyason.dokkoi.job.Prayer;

import java.util.Objects;
import java.util.UUID;

/**
 * ダメージイベントの事前検証を担当するクラス
 */
public class DamageValidator {

    private static final NamespacedKey ITEM_KEY = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);

    /**
     * 検証結果
     */
    public record ValidationResult(boolean shouldCancel, String message) {
        public static ValidationResult ok() {
            return new ValidationResult(false, null);
        }

        public static ValidationResult cancel(String message) {
            return new ValidationResult(true, message);
        }
    }

    /**
     * ゲーム状態をチェック
     */
    public static boolean isGameInProgress() {
        GameStatesManager gsm = Game.getInstance().getGameStatesManager();
        return gsm.getGameState() == GameState.IN_GAME;
    }

    /**
     * 無敵時間チェック（プレイヤー）
     */
    public static boolean hasNoDamageTicks(Player player) {
        return player.getNoDamageTicks() >= 10;
    }

    /**
     * Prayerの「もっと最強のたまたま」チェック
     */
    public static ValidationResult checkStrongestStrongestBall(Player player, GameStatesManager gsm) {
        if (gsm.getPlayerJobs().get(player.getUniqueId()) instanceof Prayer prayer) {
            if (prayer.getHasStrongestStrongestBall()) {
                return ValidationResult.cancel("§aもっと最強のたまたま§bが攻撃を許さない！");
            }
        }
        return ValidationResult.ok();
    }

    /**
     * Prayerの通常「最強のたまたま」によるダメージ軽減チェック
     * @return true: ダメージを無効化、false: ダメージ通過
     */
    public static boolean checkStrongestBallDamageReduction(Player player, GameStatesManager gsm) {
        if (!(gsm.getPlayerJobs().get(player.getUniqueId()) instanceof Prayer)) {
            return false;
        }

        PlayerInventory inventory = player.getInventory();
        double cutDamagePercent = 1.0;
        int count = 0;

        // インベントリ内の最強のたまたまをカウント
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null || !itemStack.hasItemMeta()) continue;
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) continue;

            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(ITEM_KEY, PersistentDataType.STRING)) {
                String itemName = Objects.requireNonNull(container.get(ITEM_KEY, PersistentDataType.STRING));
                if (itemName.equals(StrongestBall.id)) {
                    count++;
                }
            }
        }

        // 一個70%の確率でダメージ無効化、2個で91%、3個で97.3%
        for (int i = 0; i < count; i++) {
            cutDamagePercent *= 0.3;
        }

        if (Math.random() >= cutDamagePercent) {
            player.sendActionBar(Component.text("§a最強のたまたま§bがダメージを肩代わりした！"));
            return true;
        }

        return false;
    }

    /**
     * 一度だけダメージ無効化チェック（カタクナール、弓使いの鎧）
     */
    public static boolean checkDisableDamageOnce(Player player, GameStatesManager gsm) {
        UUID uuid = player.getUniqueId();
        if (!gsm.getIsDeactivateDamageOnce().get(uuid)) {
            return false;
        }

        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null) {
            ItemMeta meta = chestplate.getItemMeta();
            if (meta != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if (container.has(ITEM_KEY, PersistentDataType.STRING)) {
                    if (Objects.equals(container.get(ITEM_KEY, PersistentDataType.STRING), ArcherArmor.id)) {
                        player.getInventory().setChestplate(null);
                        player.sendMessage(Component.text("§a弓使いの鎧§bでダメージを無効化した！"));
                        gsm.addIsDeactivateDamageOnce(uuid, false);
                        return true;
                    }
                }
            }
        }

        player.sendMessage(Component.text("§aカタクナール§bでダメージを無効化した！"));
        gsm.addIsDeactivateDamageOnce(uuid, false);
        return true;
    }

    /**
     * Lonelyの攻撃/被ダメージ時刻を更新
     */
    public static void updateLonelyTimestamp(Player player, GameStatesManager gsm, boolean isAttacker) {
        if (gsm.getPlayerJobs().get(player.getUniqueId()) instanceof Lonely lonely) {
            if (isAttacker) {
                lonely.lastAttackedTime = System.currentTimeMillis();
            } else {
                lonely.lastDamagedTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * 攻撃者・被ダメージ者をゲーム状態に登録
     */
    public static void registerCombatants(Player attacker, Player damaged, GameStatesManager gsm) {
        if (attacker != null) {
            gsm.addAttackedPlayer(attacker.getUniqueId());
            updateLonelyTimestamp(attacker, gsm, true);
        }
        if (damaged != null) {
            gsm.addDamagedPlayer(damaged.getUniqueId());
            updateLonelyTimestamp(damaged, gsm, false);
        }
    }
}
