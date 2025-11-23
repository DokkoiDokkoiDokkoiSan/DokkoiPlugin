package org.meyason.dokkoi.goal;

import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Bomber;

import java.util.Random;

import static java.lang.Integer.min;

public class CarpetBombing extends Goal {

    public int goalNumber;

    public CarpetBombing() {super("CarpetBombing", "自爆攻撃で他人を巻き込んで殺せ！");}

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_2;
        setDamageMultiplier(this.tier.getDamageMultiplier());
        Random rand = new Random();
        this.goalNumber = rand.nextInt(1, min(3, game.getGameStatesManager().getAlivePlayers().size() - 1) + 1);
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§b指定人数： " + goalNumber + " 人");
        return;
    }

    @Override
    public boolean isAchieved() {
        if(game.getGameStatesManager().getPlayerJobs().get(player) instanceof Bomber bomber) {
            if (bomber.killCount >= goalNumber){
                this.player.sendMessage("§gよくやった。お前は立派な爆弾魔だ！");
                return true;
            }
            this.player.sendMessage("§4失敗だ。まだ目標人数に達していない。");
            return false;
        }
        return false;
    }
}
