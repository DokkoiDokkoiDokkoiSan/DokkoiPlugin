package org.meyason.dokkoi.item.matching;

import org.meyason.dokkoi.item.CustomItem;

public class JoinQueueItem extends CustomItem {

    public static final String id = "join_queue_item";

    public JoinQueueItem() {
        super(
                id,
                "§aマッチングに参加する",
                org.bukkit.inventory.ItemStack.of(org.bukkit.Material.EMERALD),
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
