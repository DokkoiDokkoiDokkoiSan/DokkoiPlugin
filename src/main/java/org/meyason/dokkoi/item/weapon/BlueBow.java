package org.meyason.dokkoi.item.weapon;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class BlueBow extends CustomItem {


    public static final String id = "blue_bow";

    public BlueBow() {
        super(id, "§aちょっと青い気がする弓", ItemStack.of(Material.BOW), 1);
        List<Component> lore = List.of(
                Component.text("§5ちょっと青くて発射レートが早い気がする弓。")
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
