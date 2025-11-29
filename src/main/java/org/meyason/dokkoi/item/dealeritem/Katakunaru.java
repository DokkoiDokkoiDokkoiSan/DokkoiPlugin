package org.meyason.dokkoi.item.dealeritem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

public class Katakunaru extends CustomItem {

    public static final String id = "katakunaru";

    public Katakunaru() {
        super(id, "§dカタクナール", ItemStack.of(Material.MELON_SEEDS), 16);
        isUnique = true;
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
