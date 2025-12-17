package org.meyason.dokkoi.menu.fortuneballmenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.entity.Clerk;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.menu.fortuneballmenu.item.FortuneBallMenuItem;
import org.meyason.dokkoi.menu.shopmenu.item.BackItem;
import org.meyason.dokkoi.menu.shopmenu.item.ForwardItem;
import org.meyason.dokkoi.menu.shopmenu.item.ShopMenuItem;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.stream.Collectors;

public class FortuneBallMenu {

    public void sendMenu(ItemStack itemStack, Player player) {

        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));

        // 自分は除外
        List<Item> items = Game.getInstance().getGameStatesManager().getJoinedPlayers().stream()
                .filter(p -> !p.equals(player.getUniqueId()))
                .map(name -> new FortuneBallMenuItem(player, itemStack, name))
                .collect(Collectors.toList());

        Gui gui = PagedGui.items()
                .setStructure(
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "x x x x x x x x x",
                        "# # # < # > # # #")
                .addIngredient('#', border)
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('<', new BackItem())
                .addIngredient('>', new ForwardItem())
                .setContent(items)
                .build();

        xyz.xenondevs.invui.window.Window window = Window.single()
                .setViewer(player)
                .setGui(gui)
                .setTitle("§a§l占いのできるたまたま")
                .build();

        window.open();
    }
}
