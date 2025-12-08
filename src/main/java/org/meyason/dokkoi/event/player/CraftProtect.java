package org.meyason.dokkoi.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class CraftProtect implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }
}
