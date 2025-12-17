package org.meyason.dokkoi.item.battleitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.event.player.DeathEvent;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class InstantDevour extends CustomItem {

    public static final String id = "instant_devour";

    public InstantDevour() {
        super(id, "§aインスタントデバウアー", ItemStack.of(Material.AMETHYST_CLUSTER), 64);
        List<Component> lore = List.of(
                Component.text("§5メキシコから輸入されてきた、持っている人間に追加体力を付与する塊。"),
                Component.text("§5本場のこのアイテムは使えば使うほどエゴが強くなるらしい。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5インベントリに存在していると追加体力を4獲得する。"),
                Component.text("§5このアイテムの効果は15個まで重複する。")
        );
        setDescription(lore);
    }


    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                item.setItemMeta(meta);
            }
            return item;
        };
    }

    public static void activate(Player player, int amount) {
        player.setMaxHealth(player.getMaxHealth() + 4 * amount);
    }

    public static void deactivate(Player player, int amount) {
        if(player.getMaxHealth() <= 4 * amount){
            DeathEvent.kill(null, player);
        }
        if(player.getHealth() > player.getMaxHealth() - 4 * amount){
            player.setHealth(player.getMaxHealth() - 4 * amount);
        }
        player.setMaxHealth(player.getMaxHealth() - 4 * amount);
    }

    public static boolean changeHP(Player player, int changedAmount) {
        // インベントリ内の個数に応じて最大15個まで効果を付与
        int currentAmount = 0;
        NamespacedKey key = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() == Material.AMETHYST_CLUSTER) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null && meta.getPersistentDataContainer().has(key) &&
                        meta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.STRING).equals(id)) {
                    currentAmount += itemStack.getAmount();
                }
            }
        }

        if(changedAmount > 0) {
            int newAmount = Math.min(currentAmount + changedAmount, 15);
            int effectiveChange = newAmount - currentAmount;
            if (effectiveChange > 0) {
                activate(player, effectiveChange);
                return true;
            }
        } else{
            int newAmount = Math.max(currentAmount + changedAmount, 0);
            int effectiveChange = currentAmount - newAmount;
            if(currentAmount > 15){
                effectiveChange = 15 - newAmount;
            }
            if (effectiveChange > 0) {
                deactivate(player, effectiveChange);
                return true;
            }
        }
        return false;
    }
}
