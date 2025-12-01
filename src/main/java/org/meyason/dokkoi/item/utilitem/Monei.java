package org.meyason.dokkoi.item.utilitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class Monei extends CustomItem {

    public static final String id = "monei";

    public Monei() {
        super(id, "§aモネイ", ItemStack.of(Material.GOLD_NUGGET), 64);
        List<Component> lore = List.of(
                Component.text("§5この土地、うん国で使われている通貨。"),
                Component.text("§5因みに1モネイは日本円で1280円らしい。")
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
