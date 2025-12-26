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

    public static void activate(Player player, ItemStack itemStack) {
        if(player.getMaxHealth() >= 100){
            return;
        }
        itemStack.setAmount(itemStack.getAmount() - 1);
        player.setMaxHealth(player.getMaxHealth() + 4);
    }

}
