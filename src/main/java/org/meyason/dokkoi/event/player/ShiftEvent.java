package org.meyason.dokkoi.event.player;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.network.PacketProcess;
import org.meyason.dokkoi.network.PacketSender;

import java.util.UUID;

public class ShiftEvent implements Listener {

    @EventHandler
    public void onShift(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if(Game.getInstance().getGameStatesManager().getGameState().equals(GameState.WAITING)) return;
        if(!Game.getInstance().getGameStatesManager().getJoinedPlayers().contains(player.getUniqueId())) return;
        for(UUID uuid : Game.getInstance().getGameStatesManager().getJoinedPlayers()){
            if(uuid.equals(player.getUniqueId())) continue;
            Player sender = Bukkit.getPlayer(uuid);
            if(sender == null || !sender.isOnline()) continue;
            if(Game.getInstance().getGameStatesManager().getAlivePlayers().contains(uuid)){
                Bukkit.getLogger().info("b");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PacketSender.sendPacket(sender, PacketProcess.hideNameTag(player, null));
                    }
                }.runTaskLater(Dokkoi.getInstance(), 2L);
            }else{
                Bukkit.getLogger().info("a");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        PacketSender.sendPacket(sender, PacketProcess.showNameTag(player, null));
                    }
                }.runTaskLater(Dokkoi.getInstance(), 2L);
            }
        }
    }
}
