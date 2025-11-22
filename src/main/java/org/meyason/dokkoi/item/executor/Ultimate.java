package org.meyason.dokkoi.item.executor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.item.CustomItem;

public class Ultimate extends CustomItem {

    public static final String id = GameItemKeyString.ULTIMATE_SKILL;

    public Ultimate() {
        super(id, "ニクトペナルティ", ItemStack.of(Material.BLACK_DYE));
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
