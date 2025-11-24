package org.meyason.dokkoi.goal;

import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Bomber;
import org.meyason.dokkoi.job.Job;

public class ComedianKiller extends Goal {

    private int killCount = 0;

    public ComedianKiller() {
        super("ComedianKiller", "芸人を自爆で3人殺せ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_3;
        setDamageMultiplier(this.tier.getDamageMultiplier());
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§bマップ内に存在する§a小島よしお§b・§aハリウッドザコシショウ§b・§aオードリー若林§b・§aパンサー尾形§b・§aビビる大木§bのどれか3人以上をパッシブスキルの自爆で倒せ！");
    }

    @Override
    public boolean isAchieved() {
        Job job = game.getGameStatesManager().getPlayerJobs().get(player);
        if(job instanceof Bomber bomber){
            this.killCount = bomber.getKillComedian();
        }
        if(this.killCount >= 3){
            this.player.sendMessage("§6よくやった！芸人を3人自爆で殺した！");
            return true;
        }
        this.player.sendMessage("§4失敗だ。芸人を3人自爆で殺せなかった。");
        return false;
    }
}
