package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.IronMaiden;

public class MaidenGazer extends Goal {

    private IronMaiden ironMaiden;

    public MaidenGazer() {
        super("§cMaiden Gazer", "§eパッシブの視線誘導を60秒間発動させろ！", Tier.TIER_3);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
        if(game.getGameStatesManager().getPlayerJobs().get(player.getUniqueId()) instanceof IronMaiden maiden){
            this.ironMaiden = maiden;
        }
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§aパッシブ『こっち見ろ！ばか！』§eで視線誘導を合計60秒間発動させろ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
    }

    public int getPoint(){
        return this.ironMaiden.getCount();
    }

    @Override
    public boolean isAchieved(){
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player.getUniqueId()))){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        if(getPoint() >= 60){
            this.player.sendMessage("§6俺はみんなが俺を見たのを見たぞ！");
            return true;
        }
        this.player.sendMessage("§c誰もお前のことなんか見ちゃいない");
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return false;
    }
}
