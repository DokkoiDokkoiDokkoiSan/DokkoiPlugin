package org.meyason.dokkoi.item.weapon;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class NormalBow extends CustomItem {

    public static final String id = "bow";

    public NormalBow() {
        super(id, "§f普通の弓", ItemStack.of(Material.BOW), 1);
        List<Component> lore = List.of(
                Component.text("§5ガチで特徴ない弓。"),
                Component.text("§5なんか弦が汗臭い。")
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
