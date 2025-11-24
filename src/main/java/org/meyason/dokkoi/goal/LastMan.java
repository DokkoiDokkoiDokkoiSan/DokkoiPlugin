package org.meyason.dokkoi.goal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

import java.util.List;

public class LastMan extends Goal {

    public LastMan() {
        super("LastMan", "最後の一人になるまで生き残れ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_1;
        setDamageMultiplier(this.tier.getDamageMultiplier());
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§b最後の一人になるまで生き残れ！");
        return;
    }

    @Override
    public boolean isAchieved() {
        List<Player> alivePlayers = this.game.getGameStatesManager().getAlivePlayers();
        if(alivePlayers.stream().noneMatch(p -> p.getUniqueId().equals(this.player.getUniqueId()))){
            this.player.sendMessage("§4お前はもう死んでいる。");
            return false;
        }
        if(alivePlayers.size() == 1){
            this.player.sendMessage("§6よくやった。お前は最後の生き残りだ！");
            return true;
        }
        this.player.sendMessage("§4失敗だ。まだほかに生きているやつがいる。");
        return false;
    }
}
