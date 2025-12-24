package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.DokkoiDatabaseAPI;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.database.DatabaseManager;
import org.meyason.dokkoi.database.models.User;
import org.meyason.dokkoi.database.repositories.MoneyRepository;
import org.meyason.dokkoi.database.repositories.UserRepository;
import org.meyason.dokkoi.exception.MoneyNotFoundException;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.exception.UserNotFoundException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameLocation;
import org.meyason.dokkoi.game.LPManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.matching.JoinQueueItem;
import org.meyason.dokkoi.item.matching.QuitQueueItem;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class LoginEvent implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String name = player.getName();
        UUID uuid = player.getUniqueId();
        Game game = Game.getInstance();
        player.getInventory().clear();
        if(game.getGameStatesManager().getGameState() == GameState.IN_GAME){
            player.kick(Component.text("§c[エラー] ゲーム進行中のため、参加できません。"));
            return;
        }else if(game.getGameStatesManager().getGameState() == GameState.WAITING || game.getGameStatesManager().getGameState() == GameState.MATCHING){
            CustomItem joinItem;
            CustomItem quitItem;
            try{
                joinItem = GameItem.getItem(JoinQueueItem.id);
                quitItem = GameItem.getItem(QuitQueueItem.id);
            } catch (NoGameItemException e) {
                player.sendMessage("§4エラーが発生しました．管理者に連絡してください：マッチング参加/退出アイテム取得失敗");
                return;
            }
            ItemStack joinItemStack = joinItem.getItem();
            ItemStack quitItemStack = quitItem.getItem();
            player.getInventory().addItem(joinItemStack);
            player.getInventory().addItem(quitItemStack);
        }

        LPManager lpManager = Dokkoi.getInstance().getLPManager();

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

        lpManager.putLP(uuid, lpManager.getLPFromDB(player));

        user.setUpdated_at(new Date());
        userRepository.updateUser(user);
        Vector lobby = GameLocation.getInstance().LobbyLocation;
        player.teleport(new Location(Bukkit.getWorld("world"), lobby.getX(), lobby.getY(), lobby.getZ()));
    }
}
