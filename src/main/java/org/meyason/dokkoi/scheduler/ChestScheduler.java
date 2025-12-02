package org.meyason.dokkoi.scheduler;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.ChestProvider;
import org.meyason.dokkoi.game.Game;

import java.util.List;

public class ChestScheduler extends BukkitRunnable {

    private World world;

    public ChestScheduler() {
        this.world = Bukkit.getWorld("world");
    }

    public List<Location> chestLocations = List.of(
        new Location(world, 4, 74, 5),
        new Location(world, 1, 74, 5),
        new Location(world, -1, 74, 3),
        new Location(world, -1, 74, 0)
    );

    public void run() {
        if(Game.getInstance().getGameStatesManager().getGameState() != GameState.IN_GAME) {
            cancel();
            return;
        }

        for(Location loc : chestLocations) {
            Block block = world.getBlockAt(loc);
            if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST){
                Chest chest = (Chest) block.getState();
                Inventory inventory = chest.getInventory();
                inventory.addItem(ChestProvider.getInstance().getRandomItem());
            }
        }


    }
}
