package org.meyason.dokkoi.event.player;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

import java.util.List;

public class DeathEvent {

    public static void kill(Player dead, Player killer){
        Game.getInstance().getGameStatesManager().removeAlivePlayer(dead);
        Game.getInstance().getGameStatesManager().getKillerList().put(killer, dead);

        dead.sendMessage("§cあなたは§l§4死亡§r§cしました");
        killer.sendMessage("§aあなたは§l§4" + dead.getName() + "§r§aを倒しました");

        dead.setGameMode(GameMode.SPECTATOR);
        dead.setHealth(20.0);

        World world = dead.getWorld();
        List<String> gameItemList = GameItemKeyString.getGameItemKeyStringHashMap();
        for(ItemStack item : dead.getInventory().getContents()){
            if(item == null) continue;
            if(item.getItemMeta() != null){
                if(GameItem.isCustomItem(item)){
                    for(String gameItemName : gameItemList){
                        CustomItem customItem = GameItem.getItem(gameItemName);
                        if(customItem != null){
                            ItemStack customItemStack = customItem.getItem();
                            customItemStack.setAmount(0);
                        }
                    }
                }
            }

            world.dropItemNaturally(dead.getLocation(), item).setPickupDelay(10);
        }
    }
}
