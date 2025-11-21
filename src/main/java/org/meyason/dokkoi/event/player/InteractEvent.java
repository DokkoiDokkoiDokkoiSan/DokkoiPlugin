package org.meyason.dokkoi.event.player;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.GachaAddict;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.gacha.GachaMachine;
import org.meyason.dokkoi.item.gacha.menu.GachaPointMenu;

import java.util.*;

public class InteractEvent implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Game game = Game.getInstance();
        if(game.getGameState() != GameState.IN_GAME || game.getGameState() != GameState.PREP) return;

        if(game.getGameState() == GameState.PREP && event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            if(block instanceof Container){
                event.setCancelled(true);
            }

        }else if(game.getGameState() == GameState.IN_GAME){
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if(item.getItemMeta() == null) {
                return;
            }
            if(GameItem.isCustomItem(item)){
                CustomItem customItem = (CustomItem)item.getItemMeta();
                if(customItem.isUnique && customItem.getId().equals(GachaMachine.id)){
                        event.setCancelled(true);
                        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
                            ItemStack newItem = GachaMachine.doGacha(event.getPlayer());
                            event.getPlayer().getInventory().addItem(Objects.requireNonNull(newItem));
                        }else{
                            GachaPointMenu menu = new GachaPointMenu();
                            menu.sendMenu(event.getPlayer());
                        }
                }
            }
        }


    }
}
