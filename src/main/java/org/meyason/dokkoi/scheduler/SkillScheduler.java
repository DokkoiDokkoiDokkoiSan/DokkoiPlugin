package org.meyason.dokkoi.scheduler;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.job.Job;

public class SkillScheduler extends BukkitRunnable {

    private Game game;
    private Player player;

    public SkillScheduler(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void run() {
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        if(!gameStatesManager.getAlivePlayers().contains(player)){
            cancel();
            return;
        }

        Job job = gameStatesManager.getPlayerJobs().get(player);
        if(gameStatesManager.getSkillCoolDownTasks().containsKey(player)){
            int remainCoolTime = job.getRemainCoolTimeSkill();
            job.setRemainCoolTimeSkill(remainCoolTime - 1);
            job.setCoolTimeSkillViewer("§eチャージ§c" + job.getRemainCoolTimeSkill() + "秒");
        }else{
            job.setCoolTimeSkillViewer("§g§lREADY!");
        }

        if(gameStatesManager.getPlayerGoals().get(player).tier != Tier.TIER_3){
            job.setCoolTimeSkillUltimateViewer("§4使用不可");
            gameStatesManager.removeUltimateSkillCoolDownTask(player);
        }else if(gameStatesManager.getUltimateSkillCoolDownTasks().containsKey(player)){
            int remainCoolTime = job.getRemainCoolTimeSkillUltimate();
            job.setRemainCoolTimeSkillUltimate(remainCoolTime - 1);
            job.setCoolTimeSkillUltimateViewer("§eチャージ§c" + job.getRemainCoolTimeSkillUltimate() + "秒");
        }else {
            job.setCoolTimeSkillUltimateViewer("§g§lREADY!");
        }

        game.updateScoreboardDisplay(player);
    }
}
