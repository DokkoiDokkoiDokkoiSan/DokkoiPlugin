package org.meyason.dokkoi.item.jobitem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.item.CustomItem;

public class Ultimate extends CustomItem {

    public static final String id = GameItemKeyString.ULTIMATE_SKILL;

    public Ultimate() {
        super(id, "", ItemStack.of(Material.YELLOW_DYE), 1);
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
