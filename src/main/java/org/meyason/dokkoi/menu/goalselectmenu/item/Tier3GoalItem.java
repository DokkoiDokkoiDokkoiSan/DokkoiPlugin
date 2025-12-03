package org.meyason.dokkoi.menu.goalselectmenu.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.LPManager;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.job.Job;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class Tier3GoalItem extends AbstractItem {

    private Job job;

    private Goal goal;

    private long cost = 0L;

    public Tier3GoalItem(Job job) {
        this.job = job;
    }

    @Override
    public ItemProvider getItemProvider(){
        List<Goal> goalList = job.getGoals();
        this.goal = null;
        for (Goal g : goalList) {
            if (g.tier == Tier.TIER_3) {
                goal = g.clone();
            }
        }
        ItemStack item = new ItemStack(Material.IRON_INGOT);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§cTier 3");
            List<Component> lore = List.of(
                    Component.text(Tier.TIER_3.getDescription()),
                    Component.text(goal.getName()),
                    Component.text(goal.getDescription())
            );
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return new ItemBuilder(item);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        LPManager lpManager = Dokkoi.getInstance().getLPManager();
        if(!lpManager.reduceLP(player.getUniqueId(), cost)) {
            player.sendMessage("§cLPが足りないため，勝利条件を選択できません。");
            return;
        }
        Game game = Game.getInstance();
        goal.setGoal(game, player);
        game.getGameStatesManager().getPlayerGoals().put(player.getUniqueId(), goal);
        job.attachGoal(goal);
        player.closeInventory();
        player.sendMessage("§bお前の勝利条件は「" + goal.getName() + "§b」だ。");
    }
}
