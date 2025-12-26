package org.meyason.dokkoi.command;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import net.kyori.adventure.text.Component;

public class InfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ実行可能です。");
            return false;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            player.sendMessage("手にアイテムを持ってください。");
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        NamespacedKey key = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        String itemName = itemMeta.getPersistentDataContainer().get(key, org.bukkit.persistence.PersistentDataType.STRING);
        if(itemName != null){
            CustomItem customItem;
            try{
                customItem = GameItem.getItem(itemName);
            } catch (NoGameItemException e){
                player.sendMessage("カスタムアイテムが見つかりません。");
                return false;
            }
            player.sendMessage("アイテム名: " + customItem.getName());
            player.sendMessage("アイテムID: " + customItem.getId());
            for (Component line: customItem.getDescription()) {
                player.sendMessage(line.toString());
            }
        } else {
            player.sendMessage("このアイテムにはカスタム名が設定されていません。");
        }
        return true;
    }
}
