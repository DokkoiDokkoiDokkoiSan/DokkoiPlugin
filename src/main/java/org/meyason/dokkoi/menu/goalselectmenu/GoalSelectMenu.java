package org.meyason.dokkoi.menu.goalselectmenu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.menu.goalselectmenu.item.*;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public class GoalSelectMenu {

    public void sendMenu(Player player){
        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));
        Job job = Game.getInstance().getGameStatesManager().getPlayerJobs().get(player.getUniqueId());
        List<Goal> goalList = job.getGoals();
        List<Tier> tiers = new ArrayList<>();
        for(Goal goal : goalList){
            if(!tiers.contains(goal.tier)) tiers.add(goal.tier);
        }
        Gui.Builder.@NotNull Normal gui;
        if(tiers.contains(Tier.TIER_1) && tiers.contains(Tier.TIER_2) && tiers.contains(Tier.TIER_3)){
            gui = Gui.normal()
                    .setStructure(
                            "a # b # c")
                    .addIngredient('#', border)

                    .addIngredient('a', new Tier1GoalItem(job))
                    .addIngredient('b', new Tier2GoalItem(job))
                    .addIngredient('c', new Tier3GoalItem(job));
        }else if(tiers.contains(Tier.TIER_1) && tiers.contains(Tier.TIER_2)){
            gui = Gui.normal()
                    .setStructure(
                            "a # b # #")
                    .addIngredient('#', border)
                    .addIngredient('a', new Tier1GoalItem(job))
                    .addIngredient('b', new Tier2GoalItem(job));
        }else if(tiers.contains(Tier.TIER_2) && tiers.contains(Tier.TIER_3)){
            gui = Gui.normal()
                    .setStructure(
                            "# # a # b")
                    .addIngredient('#', border)
                    .addIngredient('a', new Tier2GoalItem(job))
                    .addIngredient('b', new Tier3GoalItem(job));
        }else if(tiers.contains(Tier.TIER_1) && tiers.contains(Tier.TIER_3)){
            gui = Gui.normal()
                    .setStructure(
                            "a # # # b")
                    .addIngredient('#', border)
                    .addIngredient('a', new Tier1GoalItem(job))
                    .addIngredient('b', new Tier3GoalItem(job));
        }else if(tiers.contains(Tier.TIER_1)){
            gui = Gui.normal()
                    .setStructure(
                            "a # # # #")
                    .addIngredient('#', border)
                    .addIngredient('a', new Tier1GoalItem(job));
        }else if(tiers.contains(Tier.TIER_2)){
            gui = Gui.normal()
                    .setStructure(
                            "#  # a # #")
                    .addIngredient('#', border)
                    .addIngredient('a', new Tier2GoalItem(job));
        }else if(tiers.contains(Tier.TIER_3)){
            gui = Gui.normal()
                    .setStructure(
                            "# # # # a")
                    .addIngredient('#', border)
                    .addIngredient('a', new Tier3GoalItem(job));
        }else{
            player.sendMessage("§c目標が設定されていません。");
            return;
        }

        xyz.xenondevs.invui.window.Window window = Window.single()
                .setViewer(player)
                .setGui(gui.build())
                .setTitle("勝利条件選択")
                .build();

        window.open();
    }
}
