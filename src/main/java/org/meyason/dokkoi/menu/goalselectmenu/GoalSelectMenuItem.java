package org.meyason.dokkoi.menu.goalselectmenu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;


public class GoalSelectMenuItem extends CustomItem {

    public static final String id = "goal_select_menu_item";

    public GoalSelectMenuItem() {
        super(
                id,
                "§a勝利条件選択",
                ItemStack.of(Material.PAPER),
                1
        );
        List<Component> lore = List.of(
                Component.text("§5右クリックで勝利条件を選択")
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
