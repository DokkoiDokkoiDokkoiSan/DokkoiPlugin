package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
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
import org.meyason.dokkoi.goal.Defender;
import org.meyason.dokkoi.goal.Goal;

import java.util.Objects;
import java.util.UUID;

public class LogoutEvent implements Listener {

    @EventHandler
    public void onLogout(PlayerQuitEvent event){
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        DatabaseManager databaseManager = DokkoiDatabaseAPI.getInstance().getDatabaseManager();
        UserRepository userRepository = databaseManager.getUserRepository();
        MoneyRepository moneyRepository = databaseManager.getMoneyRepository();

        Long inGameLP = gameStatesManager.getLPFromUUID(playerUniqueId);
        User user;
        try {
            user = userRepository.getUserFromUUID(playerUniqueId);
            moneyRepository.updateMoneyFromLP(user, inGameLP);
        } catch (UserNotFoundException e) {
            player.sendMessage(Component.text("§cユーザーデータの取得に失敗しました。ログアウト時のLP反映に失敗しました。"));
        } catch (MoneyNotFoundException e) {
            player.sendMessage(Component.text("§c所持金データの取得に失敗しました。ログアウト時のLP反映に失敗しました。"));
        }
        gameStatesManager.removePlayerData(event.getPlayer().getUniqueId());

        if(gameStatesManager.getGameState() == GameState.WAITING || gameStatesManager.getGameState() == GameState.MATCHING || gameStatesManager.getGameState() == GameState.END) {
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
        }
    }
}
