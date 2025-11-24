package org.meyason.dokkoi.goal;

import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.IronMaiden;

public class MaidenGazer extends Goal {

    private IronMaiden ironMaiden;

    public MaidenGazer() {
        super("Maiden Gazer", "パッシブの視線誘導を60秒間発動させろ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_3;
        setDamageMultiplier(this.tier.getDamageMultiplier());
        if(game.getGameStatesManager().getPlayerJobs().get(player) instanceof IronMaiden maiden){
            this.ironMaiden = maiden;
        }
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§bパッシブ『こっち見ろ！ばか！』で視線誘導を合計60秒間発動させろ！");
    }

    public int getPoint(){
        return this.ironMaiden.getCount();
    }

    @Override
    public boolean isAchieved(){
        if(getPoint() >= 60){
            this.player.sendMessage("§6みんなが俺のことを見たのを見たぞ！");
            return true;
        }
        this.player.sendMessage("§4誰もお前のことなんか見ちゃいない");
        return false;
    }
}
