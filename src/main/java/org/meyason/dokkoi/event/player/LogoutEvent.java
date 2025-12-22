package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.DokkoiDatabaseAPI;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.database.DatabaseManager;
import org.meyason.dokkoi.database.models.User;
import org.meyason.dokkoi.database.repositories.MoneyRepository;
import org.meyason.dokkoi.database.repositories.UserRepository;
import org.meyason.dokkoi.exception.MoneyNotFoundException;
import org.meyason.dokkoi.exception.UserNotFoundException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.LPManager;
import org.meyason.dokkoi.goal.Defender;
import org.meyason.dokkoi.goal.Goal;

import java.util.Objects;
import java.util.UUID;

public class LogoutEvent implements Listener {

    @EventHandler
    public void onLogout(PlayerQuitEvent event){
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        LPManager lpManager = Dokkoi.getInstance().getLPManager();
        Player player = event.getPlayer();
        player.getInventory().clear();
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        Long inGameLP = lpManager.getLP(playerUniqueId);
        lpManager.updateLP(playerUniqueId, inGameLP);
        lpManager.removeLPData(playerUniqueId);

        gameStatesManager.removePlayerData(event.getPlayer().getUniqueId());

        if(gameStatesManager.getGameState() == GameState.END) {
            return;
        }else if (gameStatesManager.getGameState() == GameState.PREP || gameStatesManager.getGameState() == GameState.IN_GAME) {
            if(gameStatesManager.getAlivePlayers().size() < game.minimumGameStartPlayers){
                Bukkit.getServer().broadcast(Component.text("最小人数に満たないため、マッチを中断します。"));
                game.endGame();
            }

            if(gameStatesManager.getPlayerGoals().containsValue(GoalList.DEFENDER)){
                Defender defender = null;
                Player defenderPlayer = null;
                for(UUID uuid : gameStatesManager.getAlivePlayers()){
                    Goal goal = gameStatesManager.getPlayerGoals().get(uuid);
                    if(goal instanceof Defender def){
                        Player p = Bukkit.getPlayer(uuid);
                        if(p == null){continue;}
                        defenderPlayer = p;
                        defender = def;
                        break;
                    }
                }
                if(defenderPlayer == null){return;}

                Objects.requireNonNull(defender).setTargetPlayer();
                defenderPlayer.sendMessage(Component.text("§6あなたの守護対象がログアウトしたため、新たに守護対象を選定しました。"));
            }
        }else if(gameStatesManager.getGameState() == GameState.WAITING || gameStatesManager.getGameState() == GameState.MATCHING){
            game.removeFromMatchQueue(player);

        }
    }
}
