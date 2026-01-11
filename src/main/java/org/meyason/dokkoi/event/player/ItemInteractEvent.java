package org.meyason.dokkoi.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goalitem.*;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;
import org.meyason.dokkoi.item.matching.JoinQueueItem;
import org.meyason.dokkoi.item.matching.QuitQueueItem;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenu;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenuItem;

public class ItemInteractEvent{

    public static void onItemInteract(PlayerInteractEvent event, String itemID, String itemSerial, CustomItem customItem){
        Game game = Game.getInstance();
        Player player = event.getPlayer();

        if(game.getGameStatesManager().getGameState() == GameState.WAITING || game.getGameStatesManager().getGameState() == GameState.MATCHING){

            if(itemID.equals(JoinQueueItem.id)){
                event.setCancelled(true);
                Game.getInstance().addToMatchQueue(player);
            }else if(itemID.equals(QuitQueueItem.id)){
                event.setCancelled(true);
                Game.getInstance().removeFromMatchQueue(player);
            }


        }else if(game.getGameStatesManager().getGameState() == GameState.PREP){

            if(itemID.equals(GoalSelectMenuItem.id)) {
                if (game.getGameStatesManager().getPlayerGoals().get(player.getUniqueId()) != null) {
                    player.sendMessage("§c既に勝利条件が選択されています。");
                    event.setCancelled(true);
                    return;
                }
                GoalSelectMenu goalSelectMenu = new GoalSelectMenu();
                goalSelectMenu.sendMenu(event.getPlayer());
                event.setCancelled(true);
            }

        }else if(game.getGameStatesManager().getGameState() == GameState.IN_GAME) {
            if(GameItem.getItem(itemID) instanceof InteractHooker hooker){
                if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                    return;
                }
                if (!customItem.hasSerialNumber && itemSerial != null){
                    CustomItem serialItem = game.getGameStatesManager().getCustomItemFromSerial(itemSerial);
                    if (!(serialItem.hasSerialNumber)) {
                        return;
                    }
                }
                event.setCancelled(true);
                hooker.onInteract(event);
            }
        }
    }
}
