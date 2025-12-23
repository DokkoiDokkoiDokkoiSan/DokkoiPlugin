package org.meyason.dokkoi.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.entity.GameEntity;
import org.meyason.dokkoi.entity.GameEntityManager;
import org.meyason.dokkoi.entity.Skeleton;
import org.meyason.dokkoi.game.GameLocation;

import java.util.List;

public class SkeletonSpawn {

    public static void spawnSkeletons() {
        List<Vector> spawnPoints = GameLocation.skeletonSpawnLocations;

        for (Vector point : spawnPoints) {
            Location spawnLocation = new Location(
                    Bukkit.getWorld("world"),
                    point.getX(),
                    point.getY(),
                    point.getZ()
            );
            GameEntity gameEntity = GameEntity.getGameEntityFromId("skeleton");
            Skeleton skeleton = (Skeleton) gameEntity;
            GameEntityManager.spawnSkeleton(spawnLocation, skeleton);
        }
    }
}
