package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;

public class LogoutEvent implements Listener {

    @EventHandler
    public void onLogout(PlayerQuitEvent event){
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        if(gameStatesManager.getGameState() == GameState.WAITING || gameStatesManager.getGameState() == GameState.MATCHING || gameStatesManager.getGameState() == GameState.END) {
            return;
        }else if (gameStatesManager.getGameState() == GameState.PREP || gameStatesManager.getGameState() == GameState.IN_GAME) {
            gameStatesManager.removePlayerData(event.getPlayer());
            if(gameStatesManager.getAlivePlayers().size() < game.minimumGameStartPlayers){
                Bukkit.getServer().broadcast(Component.text("最小人数に満たないため、マッチを中断します。"));
                game.endGame();
            }
        }
    }
}
