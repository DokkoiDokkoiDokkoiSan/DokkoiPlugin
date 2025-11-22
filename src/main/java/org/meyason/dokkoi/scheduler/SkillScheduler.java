package org.meyason.dokkoi.scheduler;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;

public class SkillScheduler extends BukkitRunnable {

    public void run() {
        Game game = Game.getInstance();
    }

    public static  void chargeUltimateSkill(Player player, GameStatesManager gameStatesManager){
        BukkitRunnable ultimateInitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !gameStatesManager.getUltimateSkillCoolDownTasks().containsKey(player)) {
                    cancel();
                    return;
                }
                gameStatesManager.removeUltimateSkillCoolDownTask(player);
            }
        };
        ultimateInitTask.runTaskLater(Dokkoi.getInstance(), gameStatesManager.getPlayerJobs().get(player).getCoolTimeSkillUltimate() * 20L);
        gameStatesManager.addUltimateSkillCoolDownTask(player, ultimateInitTask);
    }

    public static void chargeSkill(Player player, GameStatesManager gameStatesManager){
        BukkitRunnable skillInitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !gameStatesManager.getSkillCoolDownTasks().containsKey(player)) {
                    cancel();
                    return;
                }
                gameStatesManager.removeSkillCoolDownTask(player);
            }
        };
        skillInitTask.runTaskLater(Dokkoi.getInstance(), gameStatesManager.getPlayerJobs().get(player).getCoolTimeSkill() * 20L);
        gameStatesManager.addSkillCoolDownTask(player, skillInitTask);
    }

    public static boolean isSkillCoolDown(Player player){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        return gameStatesManager.getSkillCoolDownTasks().containsKey(player);
    }

    public static boolean isUltimateSkillCoolDown(Player player){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        return gameStatesManager.getUltimateSkillCoolDownTasks().containsKey(player);
    }
}
