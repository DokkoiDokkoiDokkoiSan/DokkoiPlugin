package org.meyason.dokkoi.scheduler;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.exception.GameStateException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.network.PacketProcess;
import org.meyason.dokkoi.network.PacketSender;

public class PacketScheduler extends BukkitRunnable {

    @Override
    public void run() {
        Bukkit.getLogger().info("test");
        for( Player player : Bukkit.getOnlinePlayers()){
            PacketContainer pk;
            boolean b = false;
            if(
                    Game.getInstance().getGameStatesManager().getGameState().equals(GameState.WAITING)
                    || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.END)
            ){
                pk = PacketProcess.showNameTag(player, null);
                b = true;
            }else if(
                    (Game.getInstance().getGameStatesManager().getGameState().equals(GameState.PREP)
                    || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.MATCHING)
                    || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.IN_GAME)
                    || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.PRE_END))
            ){
                pk = PacketProcess.hideNameTag(player, null);
            }else{
                pk = PacketProcess.showNameTag(player, null);
            }
            boolean finalB = b;
            Bukkit.getOnlinePlayers().forEach(p -> {
                Bukkit.getLogger().info("own:" + player.getName() + ",send: " + p.getName() + ", hide: " + (finalB ? "hide" : "show"));
                if(p.getUniqueId().equals(player.getUniqueId())) return;
                PacketSender.sendPacket(p, pk.deepClone());
            });
        }
    }
}
