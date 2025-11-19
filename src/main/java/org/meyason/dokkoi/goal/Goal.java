package org.meyason.dokkoi.goal;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Goal implements Cloneable {

    public final String name;
    public String description;

    public boolean isKilled;
    public int killCount;
    public List<ItemStack> initialItems;

    private Player player;
    public int maxHealth;
    public int damage;

    public Goal(String name, String description){
        this.name = name;
        this.description = description;

        this.player = null;
    }

    public abstract boolean isAchieved(Player player);
}
