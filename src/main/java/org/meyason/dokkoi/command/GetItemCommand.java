package org.meyason.dokkoi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

public class GetItemCommand implements CommandExecutor {

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

        if(args.length < 2){
            player.sendMessage("§c使用法: /getitem <item id> <amount>");
            return false;
        }

        String itemId = args[0];
        int amount = 1;
        try{
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e){
            player.sendMessage("§c使用法: /getitem <item id> <amount>");
            return false;
        }

        CustomItem item = GameItem.getItem(itemId);

        if(item == null){
            player.sendMessage("§c指定されたIDのアイテムは存在しません。");
            return false;
        }
        ItemStack itemStack = item.getItem();
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        player.sendMessage("§a" + item.getName() + "§bを" + amount + "個手に入れた！");


        return true;
    }
}
