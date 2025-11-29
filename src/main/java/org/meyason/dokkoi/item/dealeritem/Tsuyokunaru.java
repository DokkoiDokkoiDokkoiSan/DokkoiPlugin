package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class Tsuyokunaru extends CustomItem {

    public static final String id = "tsuyokunaru";

    public Tsuyokunaru() {
        super(id, "§9ツヨクナール", ItemStack.of(Material.MELON_SEEDS), 16);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5力が強くなる気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5次の攻撃の与ダメージが2増える。")
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
}
