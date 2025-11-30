package org.meyason.dokkoi.menu.goalselectmenu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.meyason.dokkoi.game.Game;
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        NamespacedKey itemKey = new NamespacedKey(JavaPlugin.getPlugin(org.meyason.dokkoi.Dokkoi.class), "item_name");
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        if (meta.getPersistentDataContainer().has(itemKey) &&
                meta.getPersistentDataContainer().get(itemKey, org.bukkit.persistence.PersistentDataType.STRING).equals(id)) {
            GoalSelectMenu goalSelectMenu = new GoalSelectMenu();
            goalSelectMenu.sendMenu(event.getPlayer());
            event.setCancelled(true);
        }
    }

}
