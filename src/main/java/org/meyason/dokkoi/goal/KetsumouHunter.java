package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.Job;

import java.util.Random;

public class KetsumouHunter extends Goal {

    private int targetKetsumouCount = 0;

    public KetsumouHunter() {
        super("§cKetsumou Hunter", "§9§lけつ毛§r§5を1～5個探せ！");
    }

    public int getTargetKetsumouCount() {
        return targetKetsumouCount;
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_3;
        setDamageMultiplier(this.tier.getDamageMultiplier());
    }

    @Override
    public void addItem() {
        int minKetsumou = 1;
        int maxKetsumou = 5;
        this.targetKetsumouCount = new Random().nextInt(minKetsumou, maxKetsumou + 1);
        this.player.sendMessage("§2マップ内に散りばめられている§9§lけつ毛§r§2を§e§l" + this.targetKetsumouCount + "個§r§2集めろ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(uuid -> uuid.equals(this.player.getUniqueId()))){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        Job job = this.game.getGameStatesManager().getPlayerJobs().get(this.player.getUniqueId());
        if(job instanceof Explorer explorer) {
            if (explorer.getHaveKetsumouCount() >= this.targetKetsumouCount) {
                this.player.sendMessage("§6よくやった！お前は立派な§9§lけつ毛§r§6ハンターだ！");
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
