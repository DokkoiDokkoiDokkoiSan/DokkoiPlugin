package org.meyason.dokkoi.job.context.key;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public final class Keys {
    public static final Key<Location> LOCATION = new Key<>("location");
    public static final Key<List<String>> LIST_STRING = new Key<>("StringList");
    public static final Key<Entity> ENTITY = new Key<>("entity");
    public static final Key<Integer> INTEGER = new Key<>("Integer");
}
