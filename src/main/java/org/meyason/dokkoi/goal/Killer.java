package org.meyason.dokkoi.goal;

import org.bukkit.entity.Player;

import org.meyason.dokkoi.game.Game;

import java.util.Random;

public class Killer extends Goal {

    public int targetKillNumber;

    public int killCount;

    public Killer() {
        super("Killer", "指定した人数のプレイヤーを殺せ。");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        int totalPlayers = game.getAlivePlayers().size();
        int maxTargetNumber = Math.max(1, totalPlayers - 1);
        Random random = new Random();
        this.targetKillNumber = random.nextInt(1, maxTargetNumber);

        this.player.sendMessage("ミッション：　指定した人数のプレイヤーを殺せ。");
        this.player.sendMessage("目標人数：　" + this.targetKillNumber + " 人");
    }

    @Override
    public boolean isAchieved() {

        return true;
    }
}
