package org.meyason.dokkoi.event.block;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
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
    public void onItemFrameBreak(PlayerItemFrameChangeEvent event) {
        Player player = event.getPlayer();
        if(Dokkoi.getInstance().isEditModePlayer(player.getUniqueId())){
            return;
        }
        event.setCancelled(true);

    }

    @EventHandler
    public void onItemFrameDamage(EntityDamageByEntityEvent event) {
        // 飛び道具系で破壊されるのを防止
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
