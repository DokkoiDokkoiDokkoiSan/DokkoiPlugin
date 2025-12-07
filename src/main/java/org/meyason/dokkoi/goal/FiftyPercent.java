package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.jobitem.gacha.StrongestBall;

public class FiftyPercent extends Goal{

    public FiftyPercent() {
        super("§bFiftyPercent", "§e生存者を半数にしろ！", Tier.TIER_2);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        this.player.sendMessage(Component.text("§e生存者を半数にしろ！"));
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b条件で指定された人数になるまで誰でも殺害可能"));
        return;
    }


    @Override
    public boolean isAchieved(boolean notify) {
        if(!this.game.getGameStatesManager().getAlivePlayers().contains(player.getUniqueId())) {
            if(notify)this.player.sendMessage(Component.text("§cお前はもう死んでいる。"));
            return false;
        }
        int aliveCount = this.game.getGameStatesManager().getAlivePlayers().size();
        int totalCount = this.game.getGameStatesManager().getJoinedPlayers().size();

        if(aliveCount <= totalCount / 2){
            if(notify)this.player.sendMessage(Component.text("§6よくやった！生存者を半額にしてやった！"));
            return true;
        }
        if(notify)this.player.sendMessage(Component.text("§eまだ生存者が多い。もっと減らせ！"));
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        int aliveCount = this.game.getGameStatesManager().getAlivePlayers().size();
        int totalCount = this.game.getGameStatesManager().getJoinedPlayers().size();
        return aliveCount > totalCount / 2;
    }


}
