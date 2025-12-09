package org.meyason.dokkoi.event.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.gun.GunStatus;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.gunitem.GunItem;

public class GunSwapEvent implements Listener {


    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        NamespacedKey gunSerialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.GUN_SERIAL);

        ItemStack oldInHand = player.getInventory().getItem(event.getPreviousSlot());
        if (oldInHand != null && oldInHand.hasItemMeta()) {
            cancelOldGun(oldInHand, player);
        }

        ItemStack newInHand = player.getInventory().getItem(event.getNewSlot());
        if (newInHand != null) {
            if (!newInHand.hasItemMeta()) {
                return;
            }
            ItemMeta newMeta = newInHand.getItemMeta();
            PersistentDataContainer newContainer = newMeta.getPersistentDataContainer();
            String itemID = newContainer.get(itemKey, PersistentDataType.STRING);
            if (itemID == null) {
                return;
            }
            String gunSerial = newContainer.get(gunSerialKey, PersistentDataType.STRING);
            if (gunSerial == null) {
                return;
            }

            CustomItem customItem;
            try{
                customItem = GameItem.getItem(itemID);
            } catch (NoGameItemException e) {
                return;
            }
            if(!(customItem instanceof GunItem newGun)){
                return;
            }

            if (!manager.isExistGunFromSerial(gunSerial)) {
                manager.registerGun(gunSerial, newGun);
            }

            GunStatus gunStatus = manager.getGunStatusFromSerial(gunSerial);

            gunStatus.updateAmmo(newGun.getGunType(), player);

            gunStatus.updateActionBar(player);
        }
    }

    private static void cancelOldGun(ItemStack oldInHand, Player player) {
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        ItemMeta oldMeta = oldInHand.getItemMeta();
        PersistentDataContainer oldContainer = oldMeta.getPersistentDataContainer();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        NamespacedKey gunSerialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.GUN_SERIAL);
        String oldItemID = oldContainer.get(itemKey, PersistentDataType.STRING);
        if (oldItemID == null) {
            return;
        }
        String oldGunSerial = oldContainer.get(gunSerialKey, PersistentDataType.STRING);
        if (oldGunSerial == null) {
            return;
        }

        CustomItem customItem;
        try{
            customItem = GameItem.getItem(oldItemID);
        } catch (NoGameItemException e) {
            return;
        }
        if (!(customItem instanceof GunItem oldGun)) {
            return;
        }

        if (!manager.isExistGunFromSerial(oldGunSerial)) {
            manager.registerGun(oldGunSerial, oldGun);
        }

        GunStatus oldGunStatus = manager.getGunStatusFromSerial(oldGunSerial);
        oldGunStatus.updateAmmo(oldGun.getGunType(), player);

        if (manager.isOnReloading(player.getUniqueId())){
            BukkitRunnable task = manager.getReloadGunTask(player.getUniqueId());
            if(task != null){
                task.cancel();
                manager.removeReloadGunTask(player.getUniqueId());
            }
            oldGunStatus.cancelReload();
            oldGunStatus.updateActionBar(player);
        }
    }
}
