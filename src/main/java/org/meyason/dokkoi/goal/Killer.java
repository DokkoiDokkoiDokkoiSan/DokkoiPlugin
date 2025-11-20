package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import org.meyason.dokkoi.game.Game;

import java.util.HashMap;
import java.util.Random;

public class Killer extends Goal {

    public int targetKillNumber;

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

        this.player.sendMessage("§b目標人数：　" + this.targetKillNumber + " 人以上");
    }

    @Override
    public boolean isAchieved() {
        //killerListの中のkeyにPlayerのUUIDが指定分含まれているかどうかを確認
        HashMap<Player, Player> killerList = game.getKillerList();
        if(!killerList.containsKey(player)){
            player.sendMessage(Component.text("§c誰も殺せなかった。"));
            return false;
        }
        int killedPlayers = 0;
        for (Player killed : killerList.values()) {
            killedPlayers++;
        }
        if (killedPlayers < targetKillNumber) {
            player.sendMessage(Component.text("§c目標人数に達しなかった。"));
            return false;
        }
        player.sendMessage("§aよくやった！目標人数を達成した。");
        return true;
    }
}
