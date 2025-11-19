package org.meyason.dokkoi.scheduler;

import org.bukkit.scheduler.BukkitRunnable;

import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.exception.GameStateException;
import org.meyason.dokkoi.game.Game;

public class Scheduler extends BukkitRunnable {

    public void run() {
        Game game = Game.getInstance();
        if (game.getGameState() == null) {
            throw new GameStateException("Game state is not set.");
        }

        switch (game.getGameState()) {
            case WAITING:
                break;
            case STARTING:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    game.prepPhase();
                }
                break;
            case PREP:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    game.startGame();
                }
                break;
            case IN_GAME:
                // IN_GAME状態の処理
                break;
            case END:
                // END状態の処理
                break;
            default:
                // その他の状態の処理
                break;
        }
        return;
    }
}
