package org.meyason.dokkoi.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CalculateAreaPlayers {

    public static List<Player> getPlayersInArea(Game game, Player exceptPlayer,  Location location, double radius) {
        List<Player> playersInArea = new ArrayList<Player>();
        for(Player p : game.getGameStatesManager().getAlivePlayers()){
            if(p.getUniqueId().equals(exceptPlayer.getUniqueId())){
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
