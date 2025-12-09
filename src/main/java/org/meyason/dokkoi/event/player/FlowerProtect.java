package org.meyason.dokkoi.event.player;

import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FlowerProtect implements Listener {

    @EventHandler
    public void onFlowerManipulate(PlayerFlowerPotManipulateEvent event) {
        event.setCancelled(true);
    }
}
