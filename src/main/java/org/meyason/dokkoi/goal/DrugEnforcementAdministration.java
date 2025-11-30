package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.jobitem.gacha.StrongestBall;
import org.meyason.dokkoi.job.DrugStore;
import org.meyason.dokkoi.job.Executor;
import org.meyason.dokkoi.job.Job;

public class DrugEnforcementAdministration extends  Goal {

    public DrugEnforcementAdministration() {
        super("§cDEA", "§e麻薬密売人を3人逮捕しろ！", Tier.TIER_3);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }


    @Override
    public void addItem() {
        this.player.sendMessage(Component.text("§eマップ内に存在する密売人にスキルを当てて3人以上逮捕せよ！"));
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().contains(this.player.getUniqueId())){
            this.player.sendMessage(Component.text("§cお前はもう死んでいる。"));
            return false;
        }
        Executor executor = (Executor) this.game.getGameStatesManager().getPlayerJobs().get(this.player.getUniqueId());
        if(executor.getArrestCount() >= 3){
            this.player.sendMessage(Component.text("§6おめでとう！街から麻薬密売人を一掃した！"));
            return true;
        }
        this.player.sendMessage(Component.text("§c街に麻薬が蔓延っている..."));
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return false;
    }

}
