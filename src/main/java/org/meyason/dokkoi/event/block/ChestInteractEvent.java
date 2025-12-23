package org.meyason.dokkoi.event.block;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Prayer;

public class ChestInteractEvent implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockInteract(PlayerInteractEvent event){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block == null) return;
        if(!(block.getState() instanceof InventoryHolder chest)) return;

        if(game.getGameStatesManager().getGameState() == GameState.PREP){
            if(!Dokkoi.getInstance().isEditModePlayer(player.getUniqueId())) {
                event.setCancelled(true);
            }

        }else if(game.getGameStatesManager().getGameState() == GameState.IN_GAME){
            if(game.getGameStatesManager().isNaito(player.getUniqueId())){
                event.setCancelled(true);
                return;
            }
            if(game.getGameStatesManager().getPlayerJobs().get(player.getUniqueId()) instanceof Prayer prayer){
                if(prayer.addLocationToAlreadyOpenedChests(chest.getInventory().getLocation())){
                    player.sendActionBar(Component.text("§b[ガチャポイント]§b このチェストは初めて開ける。"));
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0F, 1.0F);
                    prayer.addGachaPoint(1);
                }
            }
        }
    }
}
