package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

public class SkeletonSlayer extends Goal{

    private int skeletonsKilled = 0;
    public int getSkeletonsKilled() {
        return skeletonsKilled;
    }
    public void incrementSkeletonsKilled() {
        this.skeletonsKilled++;
    }

    public SkeletonSlayer() {
        super("§cBoneSlayer", "§eスケルトンを50体倒せ！", Tier.TIER_3);
    }


    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        this.player.sendMessage(Component.text("§eスケルトンを50体倒せ！"));
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved(boolean notify) {
        if(!this.game.getGameStatesManager().getAlivePlayers().contains(player.getUniqueId())) {
            if(notify)this.player.sendMessage(Component.text("§cお前はもう死んでいる。"));
            return false;
        }
        if(skeletonsKilled >= 50){
            if(notify)this.player.sendMessage(Component.text("§6よくやった！スケルトンを50体倒したな！"));
            return true;
        }
        if(notify)this.player.sendMessage(Component.text("§cまだ十分な数のスケルトンを倒していない。もっと倒せ！"));
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return false;
    }

}
