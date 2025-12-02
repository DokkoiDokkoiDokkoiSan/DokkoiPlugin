package org.meyason.dokkoi.item.weapon;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class Arrow extends CustomItem {

    public static final String id = "arrow";

    public Arrow() {
        super(id, "§f矢", ItemStack.of(Material.ARROW), 64);
        List<Component> lore = List.of(
                Component.text("§5まとめて逝っけーｯ！！これが私の全力コ゛ロ゛ナ゛レ゛イ゛ン゛ｯ！！")
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
