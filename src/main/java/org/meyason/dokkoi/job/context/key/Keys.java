package org.meyason.dokkoi.job.context.key;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class Keys {
    public static final Key<Location> LOCATION = new Key<>("location");
    public static final Key<List<String>> LIST_STRING = new Key<>("stringList");
    public static final Key<Entity> ENTITY = new Key<>("entity");
    public static final Key<Integer> INTEGER = new Key<>("integer");
    public static final Key<Player> PLAYER = new Key<>("player");
    public static final Key<List<UUID>> LIST_UUID = new Key<>("uuidList");
    public static final Key<List<Player>> LIST_PLAYER = new Key<>("playerList");
}
