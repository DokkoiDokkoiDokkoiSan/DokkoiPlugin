package org.meyason.dokkoi.event.block;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

    @EventHandler
    public void onItemFrameDamage(EntityDamageByEntityEvent event) {
        // 額縁の中身が左クリックで飛び出すのを防ぐ
        if (event.getEntityType() == EntityType.ITEM_FRAME || event.getEntityType() == EntityType.PAINTING) {
            if (event.getDamager() instanceof Player) {
                if (Dokkoi.getInstance().isEditModePlayer(event.getDamager().getUniqueId())) {
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        // 額縁の中身を右クリックで回転させたり変更するのを防ぐ
        if (event.getRightClicked().getType() == EntityType.ITEM_FRAME) {
            if (Dokkoi.getInstance().isEditModePlayer(event.getPlayer().getUniqueId())) {
                return;
            }
            event.setCancelled(true);
        }
    }
}
