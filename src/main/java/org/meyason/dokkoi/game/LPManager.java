package org.meyason.dokkoi.game;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.DokkoiDatabaseAPI;
import org.meyason.dokkoi.database.DatabaseManager;
import org.meyason.dokkoi.database.models.User;
import org.meyason.dokkoi.database.repositories.MoneyRepository;
import org.meyason.dokkoi.database.repositories.UserRepository;
import org.meyason.dokkoi.exception.MoneyNotFoundException;
import org.meyason.dokkoi.exception.UserNotFoundException;

import java.util.HashMap;
import java.util.UUID;

public class LPManager {

    private HashMap<UUID, Long> LPMap;

    public LPManager() {
        this.LPMap = new HashMap<>();
    }

    public Long getLP(UUID player) {return LPMap.get(player);}

    public void putLP(UUID player, Long lpd) {
        LPMap.put(player, lpd);
    }

    public boolean isExistsLPData(UUID player) {return LPMap.containsKey(player);}

    public Long getLPFromDB(Player player) {
        UUID uuid = player.getUniqueId();
        DatabaseManager databaseManager = DokkoiDatabaseAPI.getInstance().getDatabaseManager();
        UserRepository userRepository = databaseManager.getUserRepository();
        MoneyRepository moneyRepository = databaseManager.getMoneyRepository();
        try {
            User user = userRepository.getUserFromUUID(uuid);
            return moneyRepository.getMoneyFromUserId(user.getId()).getMoney();
        } catch (UserNotFoundException e) {
            player.sendMessage(Component.text("§4エラーが発生しました．管理者に連絡してください：ユーザー情報取得失敗"));
        } catch (MoneyNotFoundException e) {
            player.sendMessage(Component.text("§4エラーが発生しました．管理者に連絡してください：所持金情報取得失敗"));
        }
        return 0L;
    }

    public void updateLP(UUID player, Long value) {
        this.LPMap.put(player, value);

        Player objPlayer = org.bukkit.Bukkit.getPlayer(player);
        if(objPlayer == null) {return;}

        DatabaseManager databaseManager = DokkoiDatabaseAPI.getInstance().getDatabaseManager();
        UserRepository userRepository = databaseManager.getUserRepository();
        MoneyRepository moneyRepository = databaseManager.getMoneyRepository();
        try {
            User user = userRepository.getUserFromUUID(player);
            moneyRepository.updateMoneyFromLP(user, value);
        } catch (UserNotFoundException e) {
            objPlayer.sendMessage("§4エラーが発生しました．管理者に連絡してください：ユーザー情報取得失敗");
            return;
        } catch (MoneyNotFoundException e) {
            objPlayer.sendMessage("§4エラーが発生しました．管理者に連絡してください：所持金情報取得失敗");
            return;
        }

    }

    public void addLP(UUID player, Long value){
        this.LPMap.put(player, this.LPMap.getOrDefault(player, 0L) + value);
        updateLP(player, getLP(player));
    }

    public boolean reduceLP(UUID player, Long value){
        if(player == null || !this.LPMap.containsKey(player)) {return false;}
        Long currentLP = this.LPMap.get(player);
        Long newLP = currentLP - value;
        if(newLP < 0) {return false;}
        this.LPMap.put(player, newLP);
        updateLP(player, getLP(player));
        return true;
    }

    public void removeLPData(UUID player) {
        if(!this.LPMap.containsKey(player)) {return;}
        this.LPMap.remove(player);
    }
}
