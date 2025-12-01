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

    public static List<Player> getPlayersInSight(/*Game game,*/ Player basePlayer, float sightDegree) {
        List<Player> players = new ArrayList<>();
        Location basePlayerLocation = basePlayer.getLocation();
        for(UUID uuid : Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).toList()){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || player.getUniqueId().equals(basePlayer.getUniqueId())) continue;
            Location targetLocation = player.getLocation();
            if(!targetLocation.getWorld().getName().equals(basePlayerLocation.getWorld().getName())) continue;
            // プレイヤー間距離があまりに遠い場合は切る
            if(targetLocation.distance(basePlayerLocation) > 10) continue;
            double cross = basePlayerLocation.getX() * targetLocation.getZ() - basePlayerLocation.getZ() * targetLocation.getX();
            double dot = basePlayerLocation.getX() * targetLocation.getX() + basePlayerLocation.getZ() * targetLocation.getZ();
            double angle = Math.toDegrees(Math.atan2(cross, dot));
            if(!(Math.abs(angle) <= sightDegree / 2)) continue;
            float multiple = 0.5f;
            Vector yDiff = targetLocation.toVector().subtract(basePlayerLocation.toVector()).normalize();
            Vector direction = basePlayerLocation.getDirection().normalize();
            direction.setY(yDiff.getY());
            Vector multipliedVector = direction.multiply(multiple);
            Vector currentVector = basePlayerLocation.toVector().add(new Vector(0, 1.68,0));// プレイヤーの目線の高さを考慮
            Vector targetBoxVectorFrom = targetLocation.toVector().add(new Vector(-0.3, 0, -0.3));
            Vector targetBoxVectorTo = targetLocation.toVector().add(new Vector(0.3, 1.8, 0.3));
            basePlayer.sendMessage("TargetX:" + targetLocation.getX() + " TargetY:" + targetLocation.getY() + " TargetZ:" + targetLocation.getZ());
            for (float i = 0f; i <= 10; i += multiple) {
                currentVector.add(multipliedVector);
                if(basePlayer.getWorld().getBlockAt(basePlayerLocation.set(currentVector.getX(), currentVector.getY(), currentVector.getZ())).getType().isOccluding()) continue;
                System.out.println(("CurrentX:" + currentVector.getX() + " CurrentY:" + currentVector.getY() + " CurrentZ:" + currentVector.getZ()));
                if(
                        //計算量多いのでまずはX,Z平面での当たり判定
                        (targetBoxVectorTo.getX() > currentVector.getX() && currentVector.getX() > targetBoxVectorFrom.getX()) &&
                        (targetBoxVectorTo.getZ() > currentVector.getZ() && currentVector.getZ() > targetBoxVectorTo.getZ())
                ){
                    continue;
                }
                basePlayer.sendMessage("e");
                if(
                        //ここでY軸方向の当たり判定
                        (targetBoxVectorTo.getY() > currentVector.getY() && currentVector.getY() > targetBoxVectorFrom.getY())
                ){
                    basePlayer.sendMessage("f");
                    players.add(player);
                    break;
                }
                //TODO: ブロックの当たり判定、あとなんかatan2の計算がおかしい死ね・寝る。
            }
        }
        System.out.println("======================================================");
        return players;
    }
}
