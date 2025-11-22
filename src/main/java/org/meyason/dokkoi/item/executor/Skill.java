package org.meyason.dokkoi.item.executor;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.item.CustomItem;

public class Skill extends CustomItem {

    public static final String id = GameItemKeyString.SKILL;

    public Skill() {
        super(id, "ギルトペナルティ", ItemStack.of(Material.BLACK_DYE));
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
