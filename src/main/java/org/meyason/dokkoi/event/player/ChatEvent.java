package org.meyason.dokkoi.event.player;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.utilitem.MamiyaPhone;
import org.meyason.dokkoi.item.utilitem.TakashimaPhone;

import java.util.List;
import java.util.UUID;

public class ChatEvent implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        List<UUID> joinedPlayers = manager.getJoinedPlayers();
        List<UUID> alivePlayers = manager.getAlivePlayers();

        if(manager.getGameState() != GameState.IN_GAME){
            return;
        }

        UUID playerUUID = player.getUniqueId();
        if(!joinedPlayers.contains(playerUUID)){
            return;
        }

        if(alivePlayers.contains(playerUUID)){
            event.setCancelled(true);
            NamespacedKey itemKey= new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta != null && itemMeta.getPersistentDataContainer().has(itemKey)){
                String itemName = itemMeta.getPersistentDataContainer().get(itemKey,  org.bukkit.persistence.PersistentDataType.STRING);
                if(itemName == null){
                    return;
                }
                if(itemName.equals(TakashimaPhone.id)){
                    if(manager.hasMamiyaPhone()){
                        UUID mamiyaOwner = manager.getPlayerWithMamiyaPhone();
                        Player mamiyaPlayer = Bukkit.getPlayer(mamiyaOwner);
                        if(mamiyaPlayer != null){
                            Component message = Component.text("§b[高島ちゃんの携帯電話]<< §6 ");
                            mamiyaPlayer.sendMessage(message.append(event.message()));
                        }
                    }
                }else if(itemName.equals(MamiyaPhone.id)){
                    if(manager.hasTakashimaPhone()){
                        UUID takashimaOwner = manager.getPlayerWithTakashimaPhone();
                        Player takashimaPlayer = Bukkit.getPlayer(takashimaOwner);
                        if(takashimaPlayer != null){
                            Component message = Component.text("§d[間宮君の携帯電話]<< §6 ");
                            takashimaPlayer.sendMessage(message.append(event.message()));
                        }
                    }
                }
            }
            return;
        }

        // 以下は観戦者のチャット処理
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
