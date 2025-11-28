package org.meyason.dokkoi.game;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.meyason.dokkoi.item.CustomItem;

public class ProjectileData {

    public Player attacker;

    public Projectile projectile;

    public String customItemName;

    public ProjectileData(Player attacker, Projectile projectile, String customItemName) {
        this.projectile = projectile;
        this.attacker = attacker;
        this.customItemName = customItemName;
    }

    public Player getAttacker() {return attacker;}

    public Projectile getProjectile() {return projectile;}

    public String getCustomItemName() {return customItemName;}

}
