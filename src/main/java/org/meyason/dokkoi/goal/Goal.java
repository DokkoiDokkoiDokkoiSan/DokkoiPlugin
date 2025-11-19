package org.meyason.dokkoi.goal;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.game.Game;

public abstract class Goal implements Cloneable {

    public final String name;
    public String description;

    public boolean isKilled;
    public int killCount;
    public List<ItemStack> initialItems;

    public Player player;
    public int maxHealth;
    public int damage;

    public Game game;

    public Goal(String name, String description) {
        this.name = name;
        this.description = description;

        this.player = null;
    }

    public String getName() {return this.name;}

    public String getDescription() {return this.description;}

    public abstract void setGoal(Game game, Player player);

    public abstract boolean isAchieved();
}
