package org.meyason.dokkoi.item.weapon;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class RedBow extends CustomItem {

    public static final String id = "red_bow";

    public RedBow() {
        super(id, "§aちょっと赤い気がする弓", ItemStack.of(Material.BOW), 1);
        List<Component> lore = List.of(
                Component.text("§5ちょっと赤くて強そうな気がする弓。")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                meta.addEnchant(Enchantment.POWER, 3, true);
                item.setItemMeta(meta);
            }
            return item;
        };
    }
}
