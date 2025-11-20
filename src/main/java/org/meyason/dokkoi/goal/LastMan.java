package org.meyason.dokkoi.goal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.meyason.dokkoi.game.Game;

import java.util.List;

public class LastMan extends Goal {

    public LastMan() {
        super("Last Man", "最後の一人になるまで生き残れ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void NoticeGoal() {
        return;
    }

    @Override
    public boolean isAchieved() {
        List<Player> alivePlayers = this.game.getAlivePlayers();
        if(alivePlayers.isEmpty()){
            this.player.sendMessage("全滅だ。");
            return false;
        }
        // プレイヤーがリストの中にいなければfalse
        if(alivePlayers.stream().noneMatch(p -> p.getUniqueId().equals(this.player.getUniqueId()))){
            this.player.sendMessage("お前はもう死んでいる。");
            return false;
        }
        if(alivePlayers.size() == 1){
            this.player.sendMessage("よくやった。お前は最後の生き残りだ！");
            return true;
        }
        this.player.sendMessage("失敗だ。まだほかに生きているやつがいる。");
        return false;
    }
}
