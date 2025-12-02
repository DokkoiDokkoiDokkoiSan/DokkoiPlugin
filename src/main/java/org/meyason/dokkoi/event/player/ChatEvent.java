package org.meyason.dokkoi.event.player;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;

import java.util.List;
import java.util.UUID;

public class ChatEvent implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        GameStatesManager statesManager = Game.getInstance().getGameStatesManager();
        List<UUID> joinedPlayers = statesManager.getJoinedPlayers();
        List<UUID> alivePlayers = statesManager.getAlivePlayers();

        if(statesManager.getGameState() != GameState.IN_GAME){
            return;
        }

        UUID playerUUID = player.getUniqueId();
        if(!joinedPlayers.contains(playerUUID)){
            return;
        }

        if(alivePlayers.contains(playerUUID)){
            event.setCancelled(true);
            // 携帯電話の処理入れる
            return;
        }

        event.setCancelled(false);
        event.viewers().clear();
        for(UUID uuid : joinedPlayers){
            if(alivePlayers.contains(uuid)){
                continue;
            }

            Player target = Bukkit.getPlayer(uuid);
            if(target != null){
                event.viewers().add(target);
            }
        }
    }
}
