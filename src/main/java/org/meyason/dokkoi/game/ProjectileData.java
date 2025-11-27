package org.meyason.dokkoi.game;

import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.job.Job;
import org.spongepowered.api.entity.living.player.Player;

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
