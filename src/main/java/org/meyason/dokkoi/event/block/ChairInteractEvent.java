package org.meyason.dokkoi.event.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.job.Sniper;

public class ChairInteractEvent implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        Game game = Game.getInstance();
        GameStatesManager manager = game.getGameStatesManager();
        Player player = event.getPlayer();
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if(block == null) return;

        if (!player.isSneaking() && player.getVehicle() != null) {
            return;
        }
        if(!(manager.getPlayerJobs().get(player.getUniqueId()) instanceof Sniper)){
            return;
        }

        if (!block.getType().toString().contains("STAIRS")) {
            return;
        }

        sitOnChair(player, block);
    }

    @EventHandler
    public void onExitChair(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.getVehicle() instanceof ArmorStand) {
            exitChair(player);
        }
    }

    private void sitOnChair(Player player, Block block) {
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Location location = block.getLocation().clone();
        location.add(0.5, 0.4, 0.5);
        Stairs stairs = (Stairs) block.getBlockData();
        BlockFace blockface = stairs.getFacing();
        float yaw = switch (blockface) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
        Stairs.Shape shape = stairs.getShape();
        switch (shape) {
            case INNER_LEFT:
                yaw -= 45;
                break;
            case INNER_RIGHT:
                yaw += 45;
                break;
            case OUTER_LEFT:
                yaw -= 45;
                break;
            case OUTER_RIGHT:
                yaw += 45;
                break;
            case STRAIGHT:
                yaw += 0;
                break;

        }

        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setRotation(yaw, 0);

        player.setRotation(yaw, 0);
        armorStand.addPassenger(player);
        manager.setSniperOnVehicle(true);
    }

    private void exitChair(Player player) {
        if (player.getVehicle() instanceof ArmorStand armorStand) {
            player.leaveVehicle();
            armorStand.remove();
            GameStatesManager manager = Game.getInstance().getGameStatesManager();
            manager.setSniperOnVehicle(false);
        }
    }
}
