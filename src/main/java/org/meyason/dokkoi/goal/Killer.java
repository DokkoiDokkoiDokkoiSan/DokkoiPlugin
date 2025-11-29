package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

import java.util.HashMap;
import java.util.List;

public class Killer extends Goal {

    public int targetKillNumber;

    public Killer() {
        super("§6Killer", "自らの手で全てのプレイヤーを殺害せよ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_1;
        setDamageMultiplier(this.tier.getDamageMultiplier());
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§2自らの手で全てのプレイヤーを殺害せよ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e全てのプレイヤー"));
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player))){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        //killerListの中のkeyにPlayerが指定分含まれているかどうかを確認
        HashMap<Player, Player> killerList = game.getGameStatesManager().getKillerList();
        if(!killerList.containsKey(player)){
            player.sendMessage(Component.text("§c誰も殺せなかった。"));
            return false;
        }
        if(game.getGameStatesManager().getAlivePlayers().size() > 1){
            player.sendMessage("§c全てのプレイヤーを殺害できなかった。");
            return false;
        }
        List<Player> killers = killerList.keySet().stream().distinct().toList();
        for(Player p : killers){
            if(!player.equals(p)){
                player.sendMessage("§c全てのプレイヤーを自らの手で殺害できなかった。");
                return false;
            }
        }
        player.sendMessage("§6よくやった！お前は全てのプレイヤーを殺害した！");
        return true;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return true;
    }
}
