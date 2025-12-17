package org.meyason.dokkoi.menu.fortuneballmenu.item;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.item.utilitem.FortuneBall;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.UUID;

public class FortuneBallMenuItem extends AbstractItem {

    private final Player player;
    private final ItemStack itemStack;
    private final Player targetPlayer;

    public FortuneBallMenuItem(Player player, ItemStack itemStack, UUID targetPlayerUUID) {
        this.player = player;
        this.itemStack = itemStack;
        this.targetPlayer = player.getServer().getPlayer(targetPlayerUUID);
    }

    @Override
    public ItemProvider getItemProvider() {
        //該当プレイヤーの頭を表示
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(this.targetPlayer);
        item.setItemMeta(meta);
        return new ItemBuilder(item).setDisplayName(this.targetPlayer.getName());
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        FortuneBall.activate(targetPlayer);
        itemStack.setAmount(0);
        event.getInventory().close();
    }
}
