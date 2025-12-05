package org.meyason.dokkoi.item.debug;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class Debug extends CustomItem {

    public static final String id = "debug";

    public Debug() {
        super(id, "§6debug", ItemStack.of(Material.STICK), 64);
        List<Component> lore = List.of(
                Component.text("§6運営専用アイテムデバッグ棒")
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
