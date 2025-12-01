package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.DrugStore;
import org.meyason.dokkoi.job.Executor;

public class MatsumotoKiyoshi extends Goal{

    public MatsumotoKiyoshi() {
        super("§bマツモトキヨシ", "§e3人のプレイヤーをコレハマール中毒にしろ！", Tier.TIER_2);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        this.player.sendMessage(Component.text("§e3人のプレイヤーをコレハマール中毒にしろ！"));
        this.player.sendMessage(Component.text("§eコレハマールを3人のプレイヤーに持たせることで条件達成"));
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
        if(drugStore.getPickCount() >= 3){
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
