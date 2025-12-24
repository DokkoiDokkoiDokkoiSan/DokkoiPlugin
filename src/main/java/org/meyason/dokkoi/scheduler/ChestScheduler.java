package org.meyason.dokkoi.scheduler;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.ChestProvider;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameLocation;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.weapon.Arrow;
import org.meyason.dokkoi.item.weapon.NormalBow;

import java.util.List;

public class ChestScheduler extends BukkitRunnable {

    private World world;

    public ChestScheduler() {
        this.world = Bukkit.getWorld("world");
    }

    public void run() {
        if(Game.getInstance().getGameStatesManager().getGameState() != GameState.IN_GAME) {
            cancel();
            return;
        }

        for(Vector vector : GameLocation.getInstance().chestLocations) {
            Location loc = new Location(world, vector.getX(), vector.getY(), vector.getZ());
            Block block = world.getBlockAt(loc);
            if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST){
                Chest chest = (Chest) block.getState();
                Inventory inventory = chest.getInventory();
                if(inventory.firstEmpty() == -1){
                    continue;
                }
                ItemStack item = ChestProvider.getInstance().getRandomItem();
                inventory.addItem(item);
                CustomItem customItem;
                try{
                    customItem = CustomItem.getItem(item);
                } catch (NoGameItemException e){
                    return;
                }
                if(customItem instanceof NormalBow){
                    CustomItem arrowItem;
                    try{
                        arrowItem = GameItem.getItem(Arrow.id);
                    } catch (NoGameItemException e){
                        return;
                    }
                    ItemStack arrow = arrowItem.getItem();
                    arrow.setAmount(32);
                    inventory.addItem(arrow);
                }

            }
        }
    }
}
