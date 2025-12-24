package org.meyason.dokkoi.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.job.Job;

import java.util.UUID;

public class SkillScheduler extends BukkitRunnable {

    private Game game;
    private Player player;

    public SkillScheduler(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void run() {
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        UUID playerUUID = player.getUniqueId();
        if(!gameStatesManager.getAlivePlayers().contains(playerUUID)){
            cancel();
            return;
        }

        Job job = gameStatesManager.getPlayerJobs().get(playerUUID);
        if(gameStatesManager.getSkillCoolDownTasks().containsKey(playerUUID)){
            int remainCoolTime = job.getRemainCoolTimeSkill();
            job.setRemainCoolTimeSkill(remainCoolTime - 1);
            job.setCoolTimeSkillViewer("§eチャージ§c" + job.getRemainCoolTimeSkill() + "秒");
        }else{
            job.setCoolTimeSkillViewer("§g§lREADY!");
        }

        if(gameStatesManager.getPlayerGoals().get(playerUUID).tier != Tier.TIER_3){
            job.setCoolTimeSkillUltimateViewer("§4使用不可");
            gameStatesManager.removeUltimateSkillCoolDownTask(playerUUID);
        }else if(gameStatesManager.getUltimateSkillCoolDownTasks().containsKey(playerUUID)) {
            int remainCoolTimeUltimate = job.getRemainCoolTimeSkillUltimate();
            job.setRemainCoolTimeSkillUltimate(remainCoolTimeUltimate - 1);
            job.setCoolTimeSkillUltimateViewer("§eチャージ§c" + job.getRemainCoolTimeSkillUltimate() + "秒");
        }else if(gameStatesManager.getPlayerJobs().get(playerUUID).getCoolTimeSkillUltimate() == -1){
            job.setCoolTimeSkillUltimateViewer("§4使用不可");
        }else {
            job.setCoolTimeSkillUltimateViewer("§g§lREADY!");
        }

        game.updateScoreboardDisplay(player);
    }
}
