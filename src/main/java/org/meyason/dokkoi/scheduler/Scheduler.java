package org.meyason.dokkoi.scheduler;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import org.meyason.dokkoi.exception.GameStateException;
import org.meyason.dokkoi.game.Game;

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
