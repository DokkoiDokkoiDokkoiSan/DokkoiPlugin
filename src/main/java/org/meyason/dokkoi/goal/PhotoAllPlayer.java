package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;

import java.util.ArrayList;
import java.util.UUID;

public class PhotoAllPlayer extends Goal{

    public ArrayList<UUID> takenPhotoPlayers;

    public PhotoAllPlayer() {
        super("PhotoAllPlayer", "生存プレイヤーを全員カメラで撮影しよう！");
        this.takenPhotoPlayers = new ArrayList<>();
    }

    public void addTakenPhotoPlayer(Player targetPlayer){
        if(!this.takenPhotoPlayers.contains(targetPlayer.getUniqueId())){
            this.takenPhotoPlayers.add(targetPlayer.getUniqueId());
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
        return true;
    }

    @Override
    public boolean isKillable(Player targetPlayer) {
        return false;
    }
}
