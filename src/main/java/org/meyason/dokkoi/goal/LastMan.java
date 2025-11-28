package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

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
        this.player.sendMessage("§2最後の一人になるまで生き残れ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e全てのプレイヤー"));
        return;
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player))){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        if(this.game.getGameStatesManager().getAlivePlayers().size() == 1){
            this.player.sendMessage("§6よくやった。お前は最後の生き残りだ！");
            return true;
        }
        this.player.sendMessage("§c失敗だ。まだほかに生きているやつがいる。");
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return true;
    }
}
