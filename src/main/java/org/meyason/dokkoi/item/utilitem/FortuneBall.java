package org.meyason.dokkoi.item.utilitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class FortuneBall extends CustomItem {

    public static final String id = "fortune_ball";

    public FortuneBall() {
        super(id, "§b占いができるたまたま", ItemStack.of(Material.HEART_OF_THE_SEA), 1);
        List<Component> lore = List.of(
                Component.text("§5指定したプレイヤーの勝利条件を知ることができる。"),
                Component.text("§5たまに嘘つくのがたまにキズ。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5使用すると指定したプレイヤー一人の勝利条件を知ることができる。"),
                Component.text("§525％の確率で全く違う勝利条件を伝えられる。")
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

    public static void activate(Player targetPlayer){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Goal goal = manager.getPlayerGoals().get(targetPlayer.getUniqueId());
        boolean isLie = Math.random() < 0.25;
        String goalName;
        if(isLie){
            List<Goal> allGoals = GoalList.getAllGoals();
            Goal fakeGoal;
            do{
                int randomIndex = (int) (Math.random() * allGoals.size());
                fakeGoal = allGoals.get(randomIndex);
            } while (fakeGoal == goal);
            goalName = fakeGoal.getName();
        } else {
            goalName = goal.getName();
        }
        targetPlayer.sendMessage(Component.text("§d" + targetPlayer.getName() + "§aの勝利条件って§e" + goalName + "§aらしいで～ｗ"));
    }
}
