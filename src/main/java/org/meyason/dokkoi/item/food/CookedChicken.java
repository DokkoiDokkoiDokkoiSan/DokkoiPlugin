package org.meyason.dokkoi.item.food;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class CookedChicken extends CustomItem {

    public static final String id  = "Cooked_Chicken";

    public CookedChicken() {
        super(id, "§a焼き鳥", ItemStack.of(Material.COOKED_CHICKEN), 64);
        List<Component> lore = List.of(
                Component.text("§5まあまあおいしい肉。")
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
