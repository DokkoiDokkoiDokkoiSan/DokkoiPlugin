package org.meyason.dokkoi.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class GameLocation {

    public static List<Location> chestLocations = List.of(
            new Location(Bukkit.getWorld("world"), -517, 73, -16),
            new Location(Bukkit.getWorld("world"), -517, 73, -14),
            new Location(Bukkit.getWorld("world"), -517, 73, -12),
            new Location(Bukkit.getWorld("world"), -517, 73, -10),
            new Location(Bukkit.getWorld("world"), -518, 73, -9),
            new Location(Bukkit.getWorld("world"), -520, 73, -9),
            new Location(Bukkit.getWorld("world"), -522, 73, -9),
            new Location(Bukkit.getWorld("world"), -524, 73, -9),
            new Location(Bukkit.getWorld("world"), -526, 73, -9)
    );

    public static List<Location> heliChairLocations = List.of(
            new Location(Bukkit.getWorld("world"), -525, 73, -18)
    );

}
