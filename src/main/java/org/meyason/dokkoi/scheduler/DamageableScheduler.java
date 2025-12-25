package org.meyason.dokkoi.scheduler;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.battleitem.ArcherArmor;
import org.meyason.dokkoi.item.jobitem.Ketsumou;
import org.meyason.dokkoi.item.jobitem.gacha.StrongestStrongestBall;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.job.Photographer;
import org.meyason.dokkoi.job.Prayer;

import java.util.UUID;

public class DamageableScheduler extends BukkitRunnable {


    private Game game;
    private Player player;

    public DamageableScheduler(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void run() {
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        UUID playerUUID = player.getUniqueId();
        if(!gameStatesManager.getAlivePlayers().contains(playerUUID)){
            cancel();
            return;
        }
        if(game.getNowTime() == 0 || gameStatesManager.getGameState() != GameState.IN_GAME){
            cancel();
            return;
        }

        Job job = gameStatesManager.getPlayerJobs().get(playerUUID);
        int ketsumouCount = Ketsumou.ketsumouCount(player);
        if(!(job instanceof Explorer)){
            if(ketsumouCount > 0){
                gameStatesManager.setIsEnableAttack(playerUUID, false);
            }
        }else{
            if(ketsumouCount == 0){
                gameStatesManager.setIsEnableAttack(playerUUID, false);
            }
        }
        if(job instanceof Photographer photographer){
            if(photographer.getTakenPhotoPlayersCount() == 0){
                gameStatesManager.setIsEnableAttack(playerUUID, false);
            }
        }else if (job instanceof Prayer prayer){
            if(prayer.getHasStrongestStrongestBall()){
                gameStatesManager.setIsEnableAttack(playerUUID, false);
            }
        }

        NamespacedKey key = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        ItemStack chestItem = player.getInventory().getChestplate();
        if(chestItem != null){
            ItemMeta meta = chestItem.getItemMeta();
            if(meta != null){
                String itemName = meta.getPersistentDataContainer().get(key,  org.bukkit.persistence.PersistentDataType.STRING);
                if(itemName != null){
                    if(itemName.equals(ArcherArmor.id)){
                        gameStatesManager.addIsDeactivateDamageOnce(player.getUniqueId(), true);
                    }
                }
            }
        }
    }
}
