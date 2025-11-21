package org.meyason.dokkoi.item.gacha.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.goal.GachaAddict;
import org.meyason.dokkoi.item.gacha.menu.item.GachaPointItem;
import org.meyason.dokkoi.item.gacha.menu.item.ShowPointItem;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public class GachaPointMenu {

    public void sendMenu(Player player){
        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));
        Gui.Builder.@NotNull Normal gui = Gui.normal()
                .setStructure(
                        "p # a b c d e f g")
                .addIngredient('#', border)

                .addIngredient('a', new GachaPointItem(Material.DIAMOND))
                .addIngredient('b', new GachaPointItem(Material.EMERALD))
                .addIngredient('c', new GachaPointItem(Material.GOLD_INGOT))
                .addIngredient('d', new GachaPointItem(Material.LAPIS_LAZULI))
                .addIngredient('e', new GachaPointItem(Material.IRON_INGOT))
                .addIngredient('f', new GachaPointItem(Material.REDSTONE))
                .addIngredient('g', new GachaPointItem(Material.COAL))
                .addIngredient('p', new ShowPointItem(player));

        xyz.xenondevs.invui.window.Window window = Window.single()
                .setViewer(player)
                .setGui(gui.build())
                .setTitle("ポイント交換所")
                .build();

        window.open();
    }
}
