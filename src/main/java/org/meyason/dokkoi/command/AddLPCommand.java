package org.meyason.dokkoi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.LPManager;

public class AddLPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("このコマンドはプレイヤーのみ実行可能です。");
            return false;
        }
        if(!player.hasPermission("addLP")){
            player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
            return false;
        }

        if(args.length < 2){
            player.sendMessage("§cコマンドの使用方法が間違っています。/addLP <player> <amount>");
            return false;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = player.getServer().getPlayerExact(targetPlayerName);
        if(targetPlayer == null){
            player.sendMessage("§c指定されたプレイヤーはオフラインです。");
            return false;
        }

        long amount;
        try{
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e){
            player.sendMessage("§c追加するLPの値は整数で指定してください。");
            return false;
        }

        LPManager lpManager = Game.getInstance().getLPManager();
        lpManager.addLP(targetPlayer.getUniqueId(), amount);

        return true;
    }
}
