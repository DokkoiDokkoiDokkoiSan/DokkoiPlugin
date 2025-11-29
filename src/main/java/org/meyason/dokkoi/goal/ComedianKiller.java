package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Bomber;
import org.meyason.dokkoi.job.Job;

public class ComedianKiller extends Goal {

    private int killCount = 0;

    public ComedianKiller() {
        super("§cComedianKiller", "芸人を自爆で3人殺せ！");
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
        this.player.sendMessage("§eマップ内に存在する§a小島よしお§e・§aハリウッドザコシショウ§e・§aオードリー若林§e・§aパンサー尾形§e・§aビビる大木§eのどれか§b3人以上§eをパッシブスキルで倒せ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player.getUniqueId()))){
            this.player.sendMessage(Component.text("§cお前はもう死んでいる。"));
            return false;
        }
        Job job = game.getGameStatesManager().getPlayerJobs().get(player.getUniqueId());
        if(job instanceof Bomber bomber){
            this.killCount = bomber.getKillComedian();
        }
        if(this.killCount >= 3){
            this.player.sendMessage("§6よくやった！芸人を3人自爆で殺した！");
            return true;
        }
        this.player.sendMessage("§c失敗だ。芸人を3人自爆で殺せなかった。");
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer) {
        return false;
    }
}
