package org.meyason.dokkoi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;

public class GameEndCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("このコマンドはプレイヤーのみ実行可能です。");
            return true;
        }
        if(!player.hasPermission("end")){
            player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
            return false;
        }
        if(Game.getInstance().getGameStatesManager().getGameState() == GameState.IN_GAME){
            Game.getInstance().preEndGame();
            return true;
        }
        player.sendMessage("§c現在ゲーム中ではありません。");

        return false;
    }
}
