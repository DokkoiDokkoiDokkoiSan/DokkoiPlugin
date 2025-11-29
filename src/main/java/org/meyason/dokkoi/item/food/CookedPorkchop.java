package org.meyason.dokkoi.item.food;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class CookedPorkchop extends CustomItem {

    public static final String id  = "cooked_porkchop";

    public CookedPorkchop() {
        super(id, "§a焼き豚", ItemStack.of(Material.COOKED_PORKCHOP), 64);
        List<Component> lore = List.of(
                Component.text("§5結構おいしい肉。")
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
