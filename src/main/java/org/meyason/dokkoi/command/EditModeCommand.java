package org.meyason.dokkoi.command;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.LPManager;

public class EditModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("このコマンドはプレイヤーのみ実行可能です。");
            return true;
        }
        if(!player.hasPermission("edit")){
            player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
            return false;
        }

        if(Dokkoi.getInstance().isEditModePlayer(player.getUniqueId())){
            Dokkoi.getInstance().removeEditModePlayer(player.getUniqueId());
            player.sendMessage("§aワールド編集モードを終了しました。");
        } else {
            Dokkoi.getInstance().addEditModePlayer(player.getUniqueId());
            player.sendMessage("§aワールド編集モードに入りました。");
            player.setGameMode(GameMode.CREATIVE);
        }

        player.getInventory().clear();

        return true;
    }
}
