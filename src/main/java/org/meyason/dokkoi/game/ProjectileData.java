package org.meyason.dokkoi.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.job.Job;

public class ProjectileData {

    public Player attacker;

    public String item;

    public ProjectileData(Player attacker, String item) {
        this.item = item;
        this.attacker = attacker;
    }

    public Player getAttacker() {return attacker;}

    public String getItem() {return item;}

}
