package org.meyason.dokkoi.command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.game.Game;

import java.util.List;

public class GoalFixCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("このコマンドはプレイヤーのみ実行可能です。");
            return true;
        }
        if(!player.hasPermission("goalfix")){
            player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
            return false;
        }
        if(args.length < 1){
            player.sendMessage("§c使用方法: /goalfix <player名> <目標名>");
            return false;
        }
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(Component.text("§cプレイヤー " + playerName + " は見つかりません。"));
            return false;
        }
        String goalName = args[1];
        if(GoalList.getAllGoalNames().contains(goalName)){
            Game.getInstance().getGameStatesManager().addGoalFixedPlayer(targetPlayer.getUniqueId(), goalName);
            sender.sendMessage(Component.text("§aプレイヤー " + playerName + " の目標を " + goalName + " に設定しました。"));
            return true;
        }else if(goalName.equals("None") || goalName.equals("none")){
            Game.getInstance().getGameStatesManager().removeGoalFixedPlayer(targetPlayer.getUniqueId());
            sender.sendMessage(Component.text("§aプレイヤー " + playerName + " の目標固定を解除しました。"));
            return true;
        }else{
            sender.sendMessage(Component.text("§c目標名 " + goalName + " は存在しません。"));
            List<String> allGoals = GoalList.getAllGoalNames();
            sender.sendMessage(Component.text("§e利用可能な目標一覧: " + String.join(", ", allGoals)));
            return false;
        }
    }
}
