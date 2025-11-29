package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.goal.Goal;

import java.util.List;

public abstract class Job implements Cloneable {

    private final String name;
    private final String description;

    public String passive_skill_name = "§aパッシブ：";
    public String normal_skill_name = "§aスキル：";
    public String ultimate_skill_name = "§aアルティメット：";

    public List<Component> passive_skill_description;
    public List<Component> normal_skill_description;
    public List<Component> ultimate_skill_description;

    public Player player;

    public Game game;

    public Goal goal;

    public List<Goal> goals;

    private int coolTimeSkill;
    private int coolTimeSkillUltimate;

    private int remainCoolTimeSkill = 0;
    private int remainCoolTimeSkillUltimate = coolTimeSkillUltimate;

    private String coolTimeSkillViewer = "";
    private String coolTimeSkillUltimateViewer = "";

    public Sound skillSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    public float skillVolume = 1.0f;
    public float skillPitch = 1.0f;
    public Sound chargeSkillSound = Sound.BLOCK_NOTE_BLOCK_CHIME;
    public float chargeSkillVolume = 1.0f;
    public float chargeSkillPitch = 1.0f;
    public Sound ultimateSkillSound = Sound.ENTITY_ENDER_DRAGON_FLAP;
    public float ultimateSkillVolume = 1.0f;
    public float ultimateSkillPitch = 1.0f;
    public Sound chargeUltimateSkillSound = Sound.BLOCK_NOTE_BLOCK_CHIME;
    public float chargeUltimateSkillVolume = 5.0f;
    public float chargeUltimateSkillPitch = 1.5f;

    public Job(String name, String description, int coolTimeSkill, int coolTimeSkillUltimate) {
        this.name = name;
        this.description = description;
        this.coolTimeSkill = coolTimeSkill;
        this.coolTimeSkillUltimate = coolTimeSkillUltimate;
    }

    public abstract void setPlayer(Game game, Player player);

    public abstract void attachGoal(Goal goal);

    public abstract void ready();

    public String getName() {return this.name;}
    public String getDescription() {return this.description;}
    public List<Goal> getGoals() {return this.goals;}
    public int getCoolTimeSkill() {return this.coolTimeSkill;}
    public void twiceCoolTimeSkill() {this.coolTimeSkill *= 2;}
    public int getCoolTimeSkillUltimate() {return this.coolTimeSkillUltimate;}
    public int getRemainCoolTimeSkill() {return this.remainCoolTimeSkill;}
    public void setRemainCoolTimeSkill(int time) {this.remainCoolTimeSkill = time;}
    public int getRemainCoolTimeSkillUltimate() {return this.remainCoolTimeSkillUltimate;}
    public void setRemainCoolTimeSkillUltimate(int time) {this.remainCoolTimeSkillUltimate = time;}
    public String getCoolTimeSkillViewer() {return this.coolTimeSkillViewer;}
    public void setCoolTimeSkillViewer(String viewer) {this.coolTimeSkillViewer = viewer;}
    public String getCoolTimeSkillUltimateViewer() {return this.coolTimeSkillUltimateViewer;}
    public void setCoolTimeSkillUltimateViewer(String viewer) {this.coolTimeSkillUltimateViewer = viewer;}

    public Job clone(){
        try {
            return (Job) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void playSoundEffectSkill(Player player){
        player.playSound(player, skillSound, skillVolume, skillPitch);
    }

    public void playSoundEffectUltimateSkill(Player player){
        player.playSound(player, ultimateSkillSound, ultimateSkillVolume, ultimateSkillPitch);
    }


    public void chargeUltimateSkill(Player player, GameStatesManager gameStatesManager){
        BukkitRunnable ultimateInitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !gameStatesManager.getUltimateSkillCoolDownTasks().containsKey(player)) {
                    cancel();
                    return;
                }
                gameStatesManager.removeUltimateSkillCoolDownTask(player);
                player.playSound(player, chargeUltimateSkillSound, chargeUltimateSkillVolume, chargeUltimateSkillPitch);
            }
        };
        ultimateInitTask.runTaskLater(Dokkoi.getInstance(), gameStatesManager.getPlayerJobs().get(player).getCoolTimeSkillUltimate() * 20L);
        gameStatesManager.addUltimateSkillCoolDownTask(player, ultimateInitTask);

    }

    public void chargeSkill(Player player, GameStatesManager gameStatesManager){
        BukkitRunnable skillInitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !gameStatesManager.getSkillCoolDownTasks().containsKey(player)) {
                    cancel();
                    return;
                }
                gameStatesManager.removeSkillCoolDownTask(player);
                player.playSound(player, chargeSkillSound, chargeSkillVolume, chargeSkillPitch);
            }
        };
        skillInitTask.runTaskLater(Dokkoi.getInstance(), gameStatesManager.getPlayerJobs().get(player).getCoolTimeSkill() * 20L);
        gameStatesManager.addSkillCoolDownTask(player, skillInitTask);
    }

    public boolean isSkillCoolDown(Player player){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        return gameStatesManager.getSkillCoolDownTasks().containsKey(player);
    }

    public boolean isUltimateSkillCoolDown(Player player){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        return gameStatesManager.getUltimateSkillCoolDownTasks().containsKey(player);
    }

}
