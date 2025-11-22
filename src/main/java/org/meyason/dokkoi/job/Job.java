package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;

import java.util.List;

public abstract class Job implements Cloneable {

    private final String name;
    private final String description;

    public String passive_skill_name = "";
    public String normal_skill_name = "";
    public String ultimate_skill_name = "";

    public List<Component> passive_skill_description;
    public List<Component> normal_skill_description;
    public List<Component> ultimate_skill_description;

    public Player player;

    public Game game;

    public List<Goal> goals;

    private int coolTimeSkill;
    private int coolTimeSkillUltimate;

    public Job(String name, String description, int coolTimeSkill, int coolTimeSkillUltimate) {
        this.name = name;
        this.description = description;
        this.coolTimeSkill = coolTimeSkill;
        this.coolTimeSkillUltimate = coolTimeSkillUltimate;
    }

    public abstract void setPlayer(Game game, Player player);

    public String getName() {return this.name;}
    public String getDescription() {return this.description;}
    public List<Goal> getGoals() {return this.goals;}
    public int getCoolTimeSkill() {return this.coolTimeSkill;}
    public void twiceCoolTimeSkill() {this.coolTimeSkill *= 2;}
    public int getCoolTimeSkillUltimate() {return this.coolTimeSkillUltimate;}

    public Job clone(){
        try {
            return (Job) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
