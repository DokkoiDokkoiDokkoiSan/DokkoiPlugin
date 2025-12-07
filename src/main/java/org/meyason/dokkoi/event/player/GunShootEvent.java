package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
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
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.gun.GunShot;
import org.meyason.dokkoi.gun.GunStatus;
import org.meyason.dokkoi.gun.constants.GunType;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.gunitem.GunItem;

public class GunShootEvent implements Listener {

    @EventHandler
    public void onGunShoot(PlayerInteractEvent event){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        NamespacedKey gunSerialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.GUN_SERIAL);

        if (!container.has(itemKey, PersistentDataType.STRING)) {
            return;
        }
        String gunSerial = container.get(gunSerialKey, PersistentDataType.STRING);
        if(gunSerial == null){
            return;
        }

        String itemID = container.get(itemKey, PersistentDataType.STRING);
        if (itemID == null) {
            return;
        }

        event.setCancelled(true);
        GameStatesManager manager = game.getGameStatesManager();

        CustomItem customItem;
        try {
            customItem = GameItem.getItem(itemID);
        } catch (NoGameItemException e) {
            return;
        }

        if(!(customItem instanceof GunItem gun)){
            return;
        }

        if(!manager.isExistGunFromSerial(gunSerial)){
            manager.registerGun(gunSerial, gun);
        }

        GunStatus gunStatus = manager.getGunStatusFromSerial(gunSerial);


        // 右クリック　射撃
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
            if (manager.isOnReloading(player.getUniqueId())) {
                return;
            }

            // 発射
            if (gunStatus.getMagazineAmmo() == 0) {
                startReload(player, gun, gunStatus);
                return;
            }

            manager.setIsShootingGunSerial(gunSerial, true);
            if(manager.isOnShootingGunTask(gunSerial)){

                BukkitRunnable shootingTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!manager.isShootingGunSerial(gunSerial)){
                            cancel();
                            manager.removeShootingGunTask(gunSerial);
                            return;
                        }

                        gunStatus.shoot();
                        GunShot gunshot = new GunShot(player, gun, gunStatus);
                        ProjectileData projectileData = new ProjectileData(player, gunshot.getProjectile(), gunSerial);
                        manager.addProjectileData(gunshot.getProjectile(), projectileData);

                        if (gunStatus.getMagazineAmmo() <= 0) {
                            manager.setIsShootingGunSerial(gunSerial, false);
                        }
                    }
                };
                shootingTask.runTaskTimer(Dokkoi.getInstance(), 0, gun.getFireRate());
                manager.addShootingGunTask(gunSerial, shootingTask);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!manager.isShootingGunSerial(gunSerial)) {
                        BukkitRunnable task = manager.getShootingGunTask(gunSerial);
                        if (task != null) {
                            task.cancel();
                            manager.removeShootingGunTask(gunSerial);
                        }
                    }
                    manager.setIsShootingGunSerial(gunSerial, true);
                }
            }.runTaskLater(Dokkoi.getInstance(), 4); // 4tick

        }else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if (!manager.isOnReloading(player.getUniqueId())) {
                startReload(player, gun, gunStatus);
            }
        }
    }

    private void startReload(Player player, GunItem gun, GunStatus gunStatus) {
        if (gunStatus.getIsReloading()) {
            return;
        }
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        GunType gunType = gun.getGunType();
        int reloadTime = gun.getReloadTime();

        boolean result = gunStatus.startReload(gunType, reloadTime, player);
        if (!result) {
            player.sendActionBar(Component.text("§c弾薬を補充してください"));
            return;
        }
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0F, 1.0F);
        BukkitRunnable reloadingTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !manager.isOnReloading(player.getUniqueId())) {
                    gunStatus.cancelReload();
                    cancel();
                    manager.removeReloadGunTask(player.getUniqueId());
                    return;
                }
                gunStatus.finishReload(gunType, player);
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1.0F, 1.0F);
                manager.removeReloadGunTask(player.getUniqueId());
                gunStatus.updateActionBar(player);
            }
        };

        reloadingTask.runTaskLater(Dokkoi.getInstance(), reloadTime / 50);
        manager.addReloadGunTask(player.getUniqueId(), reloadingTask);


        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gunStatus.getIsReloading() || !manager.isOnReloading(player.getUniqueId())) {
                    cancel();
                    gunStatus.cancelReload();
                    return;
                }
                gunStatus.updateActionBar(player);
            }
        }.runTaskTimer(Dokkoi.getInstance(), 0, 2);
    }


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

    private void cancelOldGun(ItemStack oldInHand, Player player) {
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
