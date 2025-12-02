package org.meyason.dokkoi.menu.drugrecipemenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.item.food.*;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.menu.drugrecipemenu.item.DrugRecipeItem;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class DrugRecipeMenu {

    public void sendMenu(Player player){
        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));
        Item cod = new SimpleItem(new ItemBuilder(Material.COD).setDisplayName("§a生鱈"));
        Item steak = new SimpleItem(new ItemBuilder(Material.COOKED_BEEF).setDisplayName("§aステーキ"));
        Item pumpkinPie = new SimpleItem(new ItemBuilder(Material.PUMPKIN_PIE).setDisplayName("§aパンプキンパイ"));
        Item bread = new SimpleItem(new ItemBuilder(Material.BREAD).setDisplayName("§aパン"));
        Item salmon = new SimpleItem(new ItemBuilder(Material.SALMON).setDisplayName("§a生鮭"));
        Item goldenMelon = new SimpleItem(new ItemBuilder(Material.GLISTERING_MELON_SLICE).setDisplayName("§a金のスイカ"));
        Item bakedPotato = new SimpleItem(new ItemBuilder(Material.BAKED_POTATO).setDisplayName("§aベイクドポテト"));
        Item goldenCarrot = new SimpleItem(new ItemBuilder(Material.GOLDEN_CARROT).setDisplayName("§a金のニンジン"));
        Item cookedChicken = new SimpleItem(new ItemBuilder(Material.COOKED_CHICKEN).setDisplayName("§a焼き鳥"));
        Item cookedPorkchop = new SimpleItem(new ItemBuilder(Material.COOKED_PORKCHOP).setDisplayName("§a焼き豚"));

        Job job = Game.getInstance().getGameStatesManager().getPlayerJobs().get(player.getUniqueId());
        List<Goal> goalList = job.getGoals();
        List<Tier> tiers = new ArrayList<>();
        for(Goal goal : goalList){
            if(!tiers.contains(goal.tier)) tiers.add(goal.tier);
        }
        Gui.Builder.@NotNull Normal gui = Gui.normal()
                .setStructure(
                        "a # b # c # d # e",
                        "# # # # # # # # #",
                        "f # g # h # i # i",
                        "k # l # m # n # h",
                        "p # q # q # p # k"
                        )

                .addIngredient('#', border)
                .addIngredient('f', steak)
                .addIngredient('g', cookedChicken)
                .addIngredient('h', goldenMelon)
                .addIngredient('i', goldenCarrot)
                .addIngredient('k', pumpkinPie)
                .addIngredient('l', bread)
                .addIngredient('m', bakedPotato)
                .addIngredient('n', cookedPorkchop)
                .addIngredient('p', cod)
                .addIngredient('q', salmon)

                .addIngredient('a', new DrugRecipeItem(Tsuyokunaru.id, CookedBeef.id, PumpkinPie.id, Cod.id))
                .addIngredient('b', new DrugRecipeItem(Katakunaru.id, CookedChicken.id, Bread.id, Salmon.id))
                .addIngredient('c', new DrugRecipeItem(Kizukieru.id, GlisteringMelonSlice.id, BakedPotato.id, Salmon.id))
                .addIngredient('d', new DrugRecipeItem(Hayakunaru.id, GoldenCarrot.id, CookedPorkchop.id, Cod.id))
                .addIngredient('e', new DrugRecipeItem(Korehamaru.id, GoldenCarrot.id, GlisteringMelonSlice.id, PumpkinPie.id));

        xyz.xenondevs.invui.window.Window window = Window.single()
                .setViewer(player)
                .setGui(gui.build())
                .setTitle("§aおくすり手帳")
                .build();

        window.open();
    }
}
