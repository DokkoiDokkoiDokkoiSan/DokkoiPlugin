package org.meyason.dokkoi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.entity.GameEntity;

public class SpawnEntityCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            sender.sendMessage("このコマンドはプレイヤーのみ実行可能です。");
            return true;
        }
        if(!player.hasPermission("spawnentity")){
            player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
            return false;
        }
        if(args.length == 0){
            player.sendMessage("§c使用法: /spawnentity <entity id>");
            return false;
        }

        String entityId = args[0];
        return GameEntity.spawnEntityByID(player, entityId);
    }
}
