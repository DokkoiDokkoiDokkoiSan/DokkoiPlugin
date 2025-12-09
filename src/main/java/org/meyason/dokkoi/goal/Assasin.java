package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.exception.NoAssasinTargetException;
import org.meyason.dokkoi.game.Game;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Assasin extends Goal {

    private Player targetPlayer;
    public Player getTargetPlayer() {return targetPlayer;}

    public Assasin() {
        super("§bDefender", "指定されたプレイヤーを殺せ", Tier.TIER_2);
    }

    @Override
    public void setGoal(Game game, Player player){
        this.game = game;
        this.player = player;
        this.targetPlayer = player;
    }


    @Override
    public void addItem() {
        setTargetPlayer();
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e指定されたプレイヤー§a " + targetPlayer.getName() + " §bのみ殺害可能"));
        this.player.sendMessage(Component.text("§bこれ以外を殺害するとペナルティが付与される"));
        return;
    }


    public void setTargetPlayer(){
        //プレイヤー抽選
        List<UUID> playerUUID = game.getGameStatesManager().getAlivePlayers();
        List<UUID> copyPlayers = new java.util.ArrayList<>(List.copyOf(playerUUID));
        copyPlayers.remove(this.player.getUniqueId());
        UUID targetUUID = copyPlayers.get(new Random().nextInt(playerUUID.size()-1));
        Player target = Bukkit.getPlayer(targetUUID);
        if(target != null){
            this.targetPlayer = target;
            this.player.sendMessage("§eお前のターゲットは §a" + target.getName() + " §eだ。確実に殺すぞ～～～～～！！！！！！");
            return;
        }
        throw new NoAssasinTargetException("Defenderのターゲットプレイヤーの設定に失敗しました。");
    }

    @Override
    public boolean isAchieved(boolean notify) {
        List<UUID> alivePlayerUUID = this.game.getGameStatesManager().getAlivePlayers();
        if(!alivePlayerUUID.contains(this.player.getUniqueId())){
            if(notify)this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }

        if(!alivePlayerUUID.contains(this.targetPlayer.getUniqueId())){
            if(notify)this.player.sendMessage("§aお前のターゲットはもう死んでいる。任務完了だ！");
            return true;
        }

        if(notify)this.player.sendMessage("§eまだターゲットは生きている。");
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer) {
        if(targetPlayer.equals(this.targetPlayer)){
            targetPlayer.sendMessage(Component.text("§6" + this.player.getName() + " §e「啜る～！ 殺すぞ～！」"));
            return true;
        }
        return false;
    }
}
