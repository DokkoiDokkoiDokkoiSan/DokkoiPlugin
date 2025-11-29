package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;


public class Shadow extends Goal {

    public Shadow() {
        super("§cShadow", "ゲーム終了まで誰も攻撃せず、攻撃も受けずに生き残れ！");
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
        this.player.sendMessage("§2ゲーム終了まで誰も攻撃せず、攻撃も受けずに生き残れ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player.getUniqueId()))){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        if(this.game.getGameStatesManager().getAttackedPlayers().contains(this.player.getUniqueId())){
            this.player.sendMessage("§cお前は攻撃してしまった。");
            return false;
        }
        if(this.game.getGameStatesManager().getDamagedPlayers().contains(this.player.getUniqueId())){
            this.player.sendMessage("§cお前は攻撃を受けてしまった。");
            return false;
        }
        this.player.sendMessage("§6よくやった。お前は真のぼっちだ！");
        return true;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return false;
    }
}
