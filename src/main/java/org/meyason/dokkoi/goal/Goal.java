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

    public boolean isRevived = false;

    public Goal(String name, String description, Tier tier) {
        this.name = name;
        this.description = description;
        this.tier = tier;

        this.player = null;
        this.damageMultiplier = this.tier.getDamageMultiplier();
    }

    public double getDamageMultiplier() {return damageMultiplier;}
    public void setDamageMultiplier(double damageMultiplier) {this.damageMultiplier = damageMultiplier;}

    public String getName() {return this.name;}

    public String getDescription() {return this.description;}

    public Player getPlayer() {return this.player;}

    public abstract void setGoal(Game game, Player player);

    public abstract void addItem();

    public abstract boolean isAchieved(boolean notify);

    public abstract boolean isKillable(Player targetPlayer);

    public Goal clone() {
        try {
            return (Goal) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
