package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.meyason.dokkoi.DokkoiDatabaseAPI;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.database.DatabaseManager;
import org.meyason.dokkoi.database.models.User;
import org.meyason.dokkoi.database.repositories.MoneyRepository;
import org.meyason.dokkoi.database.repositories.UserRepository;
import org.meyason.dokkoi.exception.MoneyNotFoundException;
import org.meyason.dokkoi.exception.UserNotFoundException;
import org.meyason.dokkoi.game.Game;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class LoginEvent implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        String name = player.getName();
        UUID uuid = player.getUniqueId();
        if(Game.getInstance().getGameStatesManager().getGameState() == GameState.IN_GAME){
            player.kick(Component.text("§c[エラー] ゲーム進行中のため、参加できません。"));
            return;
        }

        DatabaseManager databaseManager = DokkoiDatabaseAPI.getInstance().getDatabaseManager();
        UserRepository userRepository = databaseManager.getUserRepository();
        MoneyRepository moneyRepository = databaseManager.getMoneyRepository();

        if(!userRepository.existsUserFromUUID(uuid)){
            userRepository.createUser(player);
        }

        User user;
        try {
            user = userRepository.getUserFromUUID(uuid);
            if(!moneyRepository.existsMoneyFromUserId(user.getId())){
                moneyRepository.createMoney(user);
            }
        } catch (UserNotFoundException e) {
            player.kick(Component.text("§c[エラー] ユーザーデータの取得に失敗しました。管理者にお問い合わせください。"));
            return;
        } catch (MoneyNotFoundException e) {
            player.kick(Component.text("§c[エラー] 所持金データの取得に失敗しました。管理者にお問い合わせください。"));
            return;
        }

        if(!Objects.equals(user.getName(), name)){
            user.setName(name);
        }

        user.setUpdated_at(new Date());
        userRepository.updateUser(user);
    }
}
