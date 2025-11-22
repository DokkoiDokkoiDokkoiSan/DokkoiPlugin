package org.meyason.dokkoi.goal;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

public abstract class Goal implements Cloneable {

    private final String name;
    private final String description;

    public List<ItemStack> initialItems;

    public Player player;

    public Game game;

    public Tier tier;

    private double damageMultiplier;

    public Goal(String name, String description) {
        this.name = name;
        this.description = description;

        this.player = null;
        this.tier = null;
        this.damageMultiplier = 1.0;
    }

    public double getDamageMultiplier() {return damageMultiplier;}
    public void setDamageMultiplier(double damageMultiplier) {this.damageMultiplier = damageMultiplier;}

    public String getName() {return this.name;}

    public String getDescription() {return this.description;}

    public abstract void setGoal(Game game, Player player);

    public abstract void NoticeGoal();

    public abstract boolean isAchieved();

    public Goal clone() {
        try {
            return (Goal) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
