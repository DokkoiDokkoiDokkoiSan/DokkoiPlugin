package org.meyason.dokkoi.item.matching;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.item.CustomItem;

public class QuitQueueItem extends CustomItem {

    public static final String id = "quit_queue_item";

    public QuitQueueItem() {
        super(
                id,
                "§cマッチングから退出",
                ItemStack.of(Material.BARRIER),
                1
        );
        isUnique = true;
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                item.setItemMeta(meta);
            }
            return item;
        };
    }
}
