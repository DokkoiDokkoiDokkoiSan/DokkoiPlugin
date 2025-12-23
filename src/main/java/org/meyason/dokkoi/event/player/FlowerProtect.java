package org.meyason.dokkoi.event.player;

import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.meyason.dokkoi.Dokkoi;

public class FlowerProtect implements Listener {

    @EventHandler
    public void onFlowerManipulate(PlayerFlowerPotManipulateEvent event) {
        if(Dokkoi.getInstance().isEditModePlayer(event.getPlayer().getUniqueId())){
            return;
        }
        event.setCancelled(true);
    }
}
