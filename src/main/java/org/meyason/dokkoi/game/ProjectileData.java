package org.meyason.dokkoi.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.job.Job;

public class ProjectileData {

    public Player attacker;

    public ProjectileData(Player attacker) {
        this.attacker = attacker;
    }

    public Player getAttacker() {return attacker;}

}
