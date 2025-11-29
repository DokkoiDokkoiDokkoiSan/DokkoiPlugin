package org.meyason.dokkoi.scheduler;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import org.meyason.dokkoi.exception.GameStateException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.job.Lonely;

import java.util.UUID;

public class Scheduler extends BukkitRunnable {

    public void run() {
        Game game = Game.getInstance();
        if (game.getGameStatesManager().getGameState() == null) {
            throw new GameStateException("Game state is not set.");
        }

        switch (game.getGameStatesManager().getGameState()) {
            case WAITING:
                break;
            case MATCHING:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    Bukkit.getServer().broadcast(Component.text("§aマッチング完了。準備フェーズに移行します。"));
                    game.prepPhase();
                }
                game.updateScoreboardDisplay();
                break;
            case PREP:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    game.startGame();
                }
                boolean goalFlag = false;
                for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
                    Player player = Bukkit.getPlayer(uuid);
                    if(player == null){
                        continue;
                    }
                    if(!game.getGameStatesManager().getPlayerGoals().containsKey(uuid)){
                        goalFlag = true;
                        continue;
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 1));
                }
                if(!goalFlag){
                    Bukkit.getServer().broadcast(Component.text("§a全員が目標を設定しました。ゲームを開始します。"));
                    game.startGame();
                }
                game.updateScoreboardDisplay();
                break;
            case IN_GAME:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    game.endGame();
                }
                if(game.getGameStatesManager().getAlivePlayers().size() <= 1){
                    game.endGame();
                }
                if(game.getNowTime() % 100 == 0){
                    for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
                        Player player = Bukkit.getPlayer(uuid);
                        if(player == null || player.getGameMode().equals(GameMode.SPECTATOR)){
                            continue;
                        }
                        if(game.getGameStatesManager().getPlayerJobs().get(player.getUniqueId()) instanceof Lonely lonely){
                            if(lonely.isUltimateActive){
                                lonely.setUltimateActive(false);
                                continue;
                            }
                        }
                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 1));
                    }
                }
                game.updateScoreboardDisplay();
                break;
            case END:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    game.resetGame();
                }
                game.updateScoreboardDisplay();
                break;
            default:
                // その他の状態の処理
                break;
        }
        return;
    }
}
