package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

public class EscapeFromUnkov extends Goal{

    public EscapeFromUnkov() {
        super("§cEscape From Unkov", "ヘリコプターを見つけて脱出せよ！", Tier.TIER_3);
    }


    @Override
    public void setGoal(Game game, Player player){
        this.game = game;
        this.player = player;
    }


    @Override
    public void addItem() {
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
        if(this.game.getGameStatesManager().isSniperOnVehicle()){
            if(notify)this.player.sendMessage(Component.text("§6ヘリコプターを見つけて脱出に成功した！"));
            return true;
        } else {
            if(notify)this.player.sendMessage(Component.text("§cお前は脱出できなかった。"));
            return false;
        }
    }

    @Override
    public boolean isKillable(Player targetPlayer) {
        return false;
    }
}
