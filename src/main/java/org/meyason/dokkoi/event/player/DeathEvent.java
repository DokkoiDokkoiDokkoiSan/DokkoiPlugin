package org.meyason.dokkoi.event.player;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.goal.Police;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goal.KillerList;

import java.util.HashMap;
import java.util.List;

public class DeathEvent {

    public static void kill(Player dead, Player killer){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        manager.removeAlivePlayer(dead);
        manager.getKillerList().put(killer, dead);
        manager.removeAttackedPlayer(dead);
        manager.removeDamagedPlayer(dead);

        dead.sendMessage("§cあなたは§l§4死亡§r§cしました");
        dead.sendMessage("§eキルしたプレイヤー: §l§4" + killer.getName() + "§r§e");
        killer.sendMessage("§aあなたは§l§4" + dead.getName() + "§r§aを倒しました");

        if(manager.isEnableKillerList()){
            HashMap<Player, Goal> playerGoals = manager.getPlayerGoals();
            for(Player p : manager.getAlivePlayers()){
                Goal goal = playerGoals.get(p);
                if(goal instanceof Police police){
                    if(p.equals(killer)){continue;}
                    p.sendMessage("§c[殺すリスト] §e" + killer.getName() + " が " + dead.getName() + " を倒しました");
                    police.killerList.updateKillerList();
                }
            }
        }

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
                        if(customItem != null && customItem.isUnique){
                                item.setAmount(0);
                                break;
                        }
                    }
                }
            }

            world.dropItemNaturally(dead.getLocation(), item).setPickupDelay(10);
        }
    }
}
