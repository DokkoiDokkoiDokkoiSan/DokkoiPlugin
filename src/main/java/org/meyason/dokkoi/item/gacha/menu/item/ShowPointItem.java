package org.meyason.dokkoi.item.gacha.menu.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.GachaAddict;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.gacha.GachaMachine;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class ShowPointItem extends AbstractItem {

    private final Player player;

    public ShowPointItem(Player player) {this.player = player;}

    @Override
    public ItemProvider getItemProvider() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            Game game = Game.getInstance();
            Goal goal = game.getGameStatesManager().getPlayerGoals().get(player);
            if(goal instanceof GachaAddict gachaAddict){
                meta.setDisplayName("§a所持ポイント" + "§f: §e" + gachaAddict.getGachaPoint());
                item.setItemMeta(meta);
            }
        }
        return new ItemBuilder(item);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
    }

}
