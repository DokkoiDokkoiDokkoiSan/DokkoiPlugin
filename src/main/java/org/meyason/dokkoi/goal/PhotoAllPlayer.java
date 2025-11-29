package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

import java.util.ArrayList;
import java.util.UUID;

public class PhotoAllPlayer extends Goal{

    public ArrayList<UUID> takenPhotoPlayersUUID;

    public PhotoAllPlayer() {
        super("PhotoAllPlayer", "生存プレイヤーを全員カメラで撮影しよう！");
        this.takenPhotoPlayersUUID = new ArrayList<>();
    }

    public void addTakenPhotoPlayer(Player targetPlayer){
        if(!this.takenPhotoPlayersUUID.contains(targetPlayer.getUniqueId())){
            this.takenPhotoPlayersUUID.add(targetPlayer.getUniqueId());
        }
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
        this.player.sendMessage(Component.text("§2生存プレイヤーを全員カメラで撮影しよう！"));
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player))){
            this.player.sendMessage(Component.text("§cお前はもう死んでいる。"));
            return false;
        }
        for (UUID alivePlayer : this.game.getGameStatesManager().getAlivePlayers()){
            if(!this.takenPhotoPlayersUUID.contains(alivePlayer)) {
                this.player.sendMessage(Component.text("§c全員を撮影することができなかった。"));
                return false;
            }
        }
        this.player.sendMessage(Component.text("§6よくやった。全員を撮影したな！目標達成！"));
        return true;
    }

    @Override
    public boolean isKillable(Player targetPlayer) {
        return false;
    }
}
