package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Killer extends Goal {

    public int targetKillNumber;

    public Killer() {
        super("§6Killer", "§e自らの手で全てのプレイヤーを殺害せよ！", Tier.TIER_1);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§e自らの手で全てのプレイヤーを殺害せよ！");
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e全てのプレイヤー"));
    }

    @Override
    public boolean isAchieved(boolean notify) {
        if(!this.game.getGameStatesManager().getAlivePlayers().contains(this.player.getUniqueId())){
            if(notify)this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        //killerListの中のkeyにPlayerが指定分含まれているかどうかを確認
        HashMap<UUID, UUID> killerList = game.getGameStatesManager().getKillerList();
        if(!killerList.containsKey(player.getUniqueId())){
            if(notify)player.sendMessage(Component.text("§c誰も殺せなかった。"));
            return false;
        }
        if(game.getGameStatesManager().getAlivePlayers().size() > 1){
            if(notify)player.sendMessage("§c全てのプレイヤーを殺害できなかった。");
            return false;
        }
        List<UUID> killers = killerList.keySet().stream().distinct().toList();
        for(UUID p : killers){
            if(!player.getUniqueId().equals(p)){
                if(notify)player.sendMessage("§c全てのプレイヤーを自らの手で殺害できなかった。");
                return false;
            }
        }
        if(notify)player.sendMessage("§6よくやった！お前は全てのプレイヤーを殺害した！");
        return true;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return true;
    }
}
