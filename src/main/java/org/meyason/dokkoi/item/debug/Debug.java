package org.meyason.dokkoi.item.debug;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;

import java.util.List;
import java.util.Objects;

public class Debug extends CustomItem implements InteractHooker {

    public static final String id = "debug";

    public Debug() {
        super(id, "§6debug", ItemStack.of(Material.STICK), 64);
        List<Component> lore = List.of(
                Component.text("§6運営専用アイテムデバッグ棒")
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

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location location = Objects.requireNonNull(event.getClickedBlock()).getLocation();
            player.sendMessage(Component.text("§aクリックしたブロックの座標"));
            player.sendMessage(Component.text(location.getX() + ", " + location.getY() + ", " + location.getZ()));
        }
    }
}
