package org.meyason.dokkoi.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class GameLocation {

    public static List<Location> chestLocations = List.of(
            new Location(Bukkit.getWorld("world"), 4, 74, 5),
            new Location(Bukkit.getWorld("world"), 1, 74, 5),
            new Location(Bukkit.getWorld("world"), -1, 74, 3),
            new Location(Bukkit.getWorld("world"), -1, 74, 0)
    );

    public static List<Location> heliChairLocations = List.of(
            new Location(Bukkit.getWorld("world"), 10, 80, 10),
            new Location(Bukkit.getWorld("world"), -10, 80, -10)
    );
}
