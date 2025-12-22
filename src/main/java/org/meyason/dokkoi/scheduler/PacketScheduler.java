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


    public void run() {
        for( Player player : Bukkit.getOnlinePlayers()){
            PacketContainer pk;
            if(
                    Game.getInstance().getGameStatesManager().getGameState().equals(GameState.WAITING)
                    || Game.getInstance().getGameStatesManager().getGameState().equals(GameState.END)
            ){
                pk = PacketProcess.showNameTag(player, null);
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
            Bukkit.getOnlinePlayers().forEach(p -> {
                if(p.getUniqueId().equals(player.getUniqueId())) return;
                PacketSender.sendPacket(p, pk.deepClone());
            });
        }
    }
}
