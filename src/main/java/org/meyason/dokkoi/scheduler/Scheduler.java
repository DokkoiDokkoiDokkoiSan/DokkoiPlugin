package org.meyason.dokkoi.scheduler;

import org.bukkit.scheduler.BukkitRunnable;

import org.meyason.dokkoi.game.Game;

public class Scheduler extends BukkitRunnable {

    public void run() {
        Game game = Game.getInstance();
        if (game.getGameState() == null) {
            // 例外処理
            return;
        }

        switch (game.getGameState()) {
            case WAITING:
                // WAITING状態の処理
                break;
            case STARTING:
                // STARTING状態の処理
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
