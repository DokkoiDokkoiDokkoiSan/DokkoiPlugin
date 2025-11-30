package org.meyason.dokkoi.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CalculateAreaPlayers {

    public static List<Player> getPlayersInArea(Game game, Player exceptPlayer, Location location, double radius) {
        List<Player> playersInArea = new ArrayList<>();
        for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
            if(uuid.equals(exceptPlayer.getUniqueId())){
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

    public static List<Player> getPlayersInSight(Game game, Player basePlayer, float sightDegree) {
        List<Player> players = new ArrayList<>();
        Location basePlayerLocation = basePlayer.getLocation();
        for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;
            Location targetLocation = player.getLocation();
            // プレイヤー間距離があまりに遠い場合は切る
            if(targetLocation.distance(basePlayerLocation) <= 50) continue;
            double cross = basePlayerLocation.getX() * targetLocation.getZ() - basePlayerLocation.getZ() * targetLocation.getX();
            double dot = basePlayerLocation.getX() * targetLocation.getX() + basePlayerLocation.getZ() * targetLocation.getZ();
            double angle = Math.toDegrees(Math.atan2(cross, dot));
            if(!(Math.abs(angle) <= sightDegree / 2)) continue;
            double vectorSize = targetLocation.toVector().length();
            Vector normalizedBaseVector = basePlayerLocation.toVector().normalize();
            //TODO: player間に障害物があるかないかを判定する
        }
        return players;
    }
}
