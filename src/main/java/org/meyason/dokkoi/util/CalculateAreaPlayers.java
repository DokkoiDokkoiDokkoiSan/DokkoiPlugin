package org.meyason.dokkoi.util;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
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

    public static ArrayList<Player> getPlayersInSight(Game game, Player basePlayer, float sightDegree) {
        ArrayList<Player> players = new ArrayList<>();
        Location basePlayerLocation = basePlayer.getLocation();
        Location eyeLoc = basePlayer.getEyeLocation();
        Vector basePlayerEyeVector = eyeLoc.toVector();
        Vector basePlayerDirection = eyeLoc.getDirection().normalize();

        for (UUID uuid : game.getGameStatesManager().getAlivePlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || player.getUniqueId().equals(basePlayer.getUniqueId())) continue;
            Location targetLocation = player.getLocation();
            if (!targetLocation.getWorld().equals(basePlayerLocation.getWorld())) continue;
            if (targetLocation.distance(basePlayerLocation) > 10) continue;

            Vector toTarget = player.getEyeLocation().toVector().subtract(basePlayerEyeVector).normalize();

            double dot = basePlayerDirection.dot(toTarget);
            double angle = Math.toDegrees(Math.acos(dot));
            if (angle > sightDegree / 2) continue;

            RayTraceResult blockHit = basePlayer.getWorld().rayTraceBlocks(
                    eyeLoc,
                    toTarget,
                    10,
                    FluidCollisionMode.NEVER
            );
            if (blockHit != null) {
                double blockDist = blockHit.getHitPosition().distance(eyeLoc.toVector());
                double targetDist = targetLocation.toVector().distance(eyeLoc.toVector());
                if (blockDist < targetDist) continue;
            }

            players.add(player);
        }

        return players;
    }
}
