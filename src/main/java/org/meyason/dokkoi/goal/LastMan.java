package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

public class LastMan extends Goal {

    public LastMan() {
        super("§6Last Man", "§e最後の一人になるまで生き残れ！", Tier.TIER_1);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§e最後の一人になるまで生き残れ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e全てのプレイヤー"));
        this.player.playerListName();
        return;
    }

    @Override
    public boolean isAchieved(boolean notify) {
        if(!this.game.getGameStatesManager().getAlivePlayers().contains(this.player.getUniqueId())){
            if(notify)this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        if(this.game.getGameStatesManager().getAlivePlayers().size() == 1){
            if(notify)this.player.sendMessage("§6よくやった。お前は最後の生き残りだ！");
            return true;
        }
        if(notify)this.player.sendMessage("§c失敗だ。まだほかに生きているやつがいる。");
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return true;
    }
}
