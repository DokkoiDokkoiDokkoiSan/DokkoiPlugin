package org.meyason.dokkoi.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CalculateAreaPlayers {

    public static List<Player> getPlayersInArea(Game game, Player exceptPlayer, Location location, double radius) {
        List<Player> playersInArea = new ArrayList<>();
        UUID exceptUUID = null;
        if(exceptPlayer != null) {
            exceptUUID = exceptPlayer.getUniqueId();
        }
        for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){

            if(exceptUUID != null && uuid.equals(exceptUUID)){
                continue;
            }
            if(!game.getGameStatesManager().getAlivePlayers().contains(uuid)){
                continue;
            }
            Player p = Bukkit.getPlayer(uuid);
            if(p == null){
                continue;
            }
            Location target = p.getLocation();
            if(location.distance(target) <= radius){
                playersInArea.add(p);
            }
        }
        return playersInArea;
    }
}
