package org.meyason.dokkoi.job.context.data;

import org.bukkit.Location;

public final class LocationData implements Data<Location> {

    public static final LocationData KEY = new LocationData();

    private LocationData() {}
}
