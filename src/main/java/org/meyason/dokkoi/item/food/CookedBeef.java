package org.meyason.dokkoi.item.food;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class CookedBeef extends CustomItem {

    public static final String id  = "cooked_beef";

    public CookedBeef() {
        super(id, "§aステーキ", ItemStack.of(Material.COOKED_BEEF), 64);
        List<Component> lore = List.of(
                Component.text("§5クッソおいしい肉。")
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
