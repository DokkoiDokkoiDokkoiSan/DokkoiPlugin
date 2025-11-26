package org.meyason.dokkoi.item.food;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class GoldenCarrot extends CustomItem {

    public static final String id  = "golden_carrot";

    public GoldenCarrot() {
        super(id, "§a金のニンジン", ItemStack.of(Material.GOLDEN_CARROT), 64);
        List<Component> lore = List.of(
                Component.text("§5ちょっと美味しいニンジン。")
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
