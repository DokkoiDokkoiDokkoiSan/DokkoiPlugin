package org.meyason.dokkoi.item.goalitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class GoalMemo extends CustomItem {

    public static final String id = "goal_memo";

    public GoalMemo() {
        super(id, "§a勝利条件メモ", ItemStack.of(Material.PAPER), 1);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5右クリックで勝利条件を確認")
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
