package org.meyason.dokkoi.item.food;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class BakedPotato extends CustomItem {

    public static final String id  = "baked_potato";

    public BakedPotato() {
        super(id, "§aベイクドポテト", ItemStack.of(Material.BAKED_POTATO), 64);
        List<Component> lore = List.of(
                Component.text("§5メヤソナルド特製蒸かした芋です。")
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
