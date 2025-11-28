package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Bomber;

import java.util.Random;

import static java.lang.Integer.min;

public class CarpetBombing extends Goal {

    public int goalNumber;

    private Bomber bomber;

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
        this.player.sendMessage("§2自爆攻撃で他人を巻き込んで殺せ！");
        this.player.sendMessage("§2指定人数： §a§l" + goalNumber + "§2人");
        this.bomber = (Bomber) game.getGameStatesManager().getPlayerJobs().get(this.player);
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e" + goalNumber + " 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player))){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        if (bomber.killCount >= goalNumber){
            this.player.sendMessage("§6よくやった。お前は立派な爆弾魔だ！");
            return true;
        }
        this.player.sendMessage("§c失敗だ。まだ目標人数に達していない。");
        return false;
    }

    public int getKillCount(){
        return bomber.killCount;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        if(bomber.killCount + 1 > goalNumber){
            return false;
        }
        return true;
    }
}
