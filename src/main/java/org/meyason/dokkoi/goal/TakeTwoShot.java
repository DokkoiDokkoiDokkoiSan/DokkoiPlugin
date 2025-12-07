package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.JobList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.exception.JobDataMismatchException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.job.Photographer;

public class TakeTwoShot extends Goal{

    public TakeTwoShot() {
        super("§cTakeTwoShot", "§e2人以上のプレイヤーをカメラに収めよう！", Tier.TIER_3);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        this.player.sendMessage(Component.text("§22人以上のプレイヤーをカメラに収めよう！"));
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
    }

    @Override
    public boolean isAchieved(boolean notify) {
        if(!this.game.getGameStatesManager().getAlivePlayers().contains(this.player.getUniqueId())){
            if(notify)this.player.sendMessage(Component.text("§cお前はもう死んでいる。"));
            return false;
        }
        Job job = this.game.getGameStatesManager().getPlayerJobs().get(this.player.getUniqueId());
        if(!(job instanceof Photographer photographer)){
            throw new JobDataMismatchException(JobList.PHOTOGRAPHER, job);
        }
        if(!photographer.isTwoShotPhotoTaken()){
            this.player.sendMessage(Component.text("§c2人以上のプレイヤーをカメラに収めることができなかった。"));
            return false;
        }
        this.player.sendMessage(Component.text("§6よくやった。全員を撮影したな！目標達成！"));
        return true;
    }

    @Override
    public boolean isKillable(Player targetPlayer) {
        return false;
    }
}
