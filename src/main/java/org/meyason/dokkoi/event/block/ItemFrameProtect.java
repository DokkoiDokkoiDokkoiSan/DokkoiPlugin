package org.meyason.dokkoi.event.block;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.meyason.dokkoi.Dokkoi;

public class ItemFrameProtect implements Listener {

    @EventHandler
    public void onItemFrameBreak(HangingBreakByEntityEvent event) {
        // 額縁や絵画が壊されるのを防ぐ
        Hanging hanging = event.getEntity();
        if(Dokkoi.getInstance().isEditModePlayer(event.getRemover().getUniqueId())){
            return;
        }
        if (hanging.getType() == EntityType.ITEM_FRAME || hanging.getType() == EntityType.PAINTING) {
            event.setCancelled(true);
        }

    }
}
