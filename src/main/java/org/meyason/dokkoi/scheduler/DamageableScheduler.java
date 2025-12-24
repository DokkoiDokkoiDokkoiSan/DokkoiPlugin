package org.meyason.dokkoi.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.jobitem.Ketsumou;
import org.meyason.dokkoi.item.jobitem.gacha.StrongestStrongestBall;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.job.Photographer;
import org.meyason.dokkoi.job.Prayer;

import java.util.UUID;

public class DamageableScheduler extends BukkitRunnable {


    private Game game;
    private Player player;

    public DamageableScheduler(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void run() {
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        UUID playerUUID = player.getUniqueId();
        if(!gameStatesManager.getAlivePlayers().contains(playerUUID)){
            cancel();
            return;
        }
        Job job = gameStatesManager.getPlayerJobs().get(playerUUID);
        if(game.getNowTime() > 500){
            gameStatesManager.setIsEnableAttack(playerUUID, false);
        }else if(game.getNowTime() == 500){
            gameStatesManager.setIsEnableAttack(playerUUID, true);
        }else if(game.getNowTime() == 0 || gameStatesManager.getGameState() != GameState.IN_GAME){
            cancel();
            return;
        }
        int ketsumouCount = Ketsumou.ketsumouCount(player);
        if(!(job instanceof Explorer)){
            if(ketsumouCount > 0){
                gameStatesManager.setIsEnableAttack(playerUUID, false);
            }
        }else{
            if(ketsumouCount == 0){
                gameStatesManager.setIsEnableAttack(playerUUID, false);
            }
        }
        if(job instanceof Photographer photographer){
            if(photographer.getTakenPhotoPlayersCount() == 0){
                gameStatesManager.setIsEnableAttack(playerUUID, false);
            }
        }else if (job instanceof Prayer prayer){
            if(prayer.getHasStrongestStrongestBall()){
                gameStatesManager.setIsEnableAttack(playerUUID, false);
            }
        }
    }
}
