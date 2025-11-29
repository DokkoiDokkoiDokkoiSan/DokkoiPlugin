package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.Job;

public class KetsumouPirate extends Goal {


    private int targetKetsumouCount = 9;

    public KetsumouPirate() {
        super("§bKetsumou Hunter", "§9§lけつ毛§r§5を9個探せ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_2;
        setDamageMultiplier(this.tier.getDamageMultiplier());
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§eマップ内に散りばめられている§9§lけつ毛§r§eを§e§l" + this.targetKetsumouCount + "個§r§e集めろ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player.getUniqueId()))){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        Job job = this.game.getGameStatesManager().getPlayerJobs().get(this.player.getUniqueId());
        if(job instanceof Explorer explorer) {
            if (explorer.getHaveKetsumouCount() >= this.targetKetsumouCount) {
                this.player.sendMessage("§6よくやった！お前は立派な§9§lけつ毛§r§6王だ！");
                return true;
            }
        }
        this.player.sendMessage(Component.text("§c目標数の§9§lけつ毛§r§cを集められなかった。"));
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return false;
    }
}
