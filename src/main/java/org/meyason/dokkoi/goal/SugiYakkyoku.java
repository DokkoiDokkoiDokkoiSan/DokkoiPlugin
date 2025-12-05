package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.DrugStore;
import org.meyason.dokkoi.job.Executor;

public class SugiYakkyoku extends Goal {

    public SugiYakkyoku() {
        super("§cスギ薬局", "§e密売人に薬を10回売れ！", Tier.TIER_3);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        this.player.sendMessage(Component.text("§e密売人に薬を10回売れ！"));
        this.player.sendMessage(Component.text("§eマップ内に5人存在する密売人に指定された薬を10個売れば条件達成。"));
        this.player.sendMessage(Component.text("§eただし密売人1人につき、5個までしか売れない。"));
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved(boolean notify) {
        if(!this.game.getGameStatesManager().getAlivePlayers().contains(this.player.getUniqueId())){
            if(notify)this.player.sendMessage(Component.text("§cお前はもう死んでいる。"));
            return false;
        }
        DrugStore drugStore = (DrugStore) this.game.getGameStatesManager().getPlayerJobs().get(this.player.getUniqueId());
        if(drugStore.getSellCount() >= 10){
            if(notify)this.player.sendMessage(Component.text("§6よくやった！俺たちのシマはお前に任せたぜ！"));
            return true;
        }
        if(notify)this.player.sendMessage(Component.text("§c街に麻薬が広まっていない……"));
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return false;
    }
}
