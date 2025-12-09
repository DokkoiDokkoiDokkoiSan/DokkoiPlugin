package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.gun.GunShot;
import org.meyason.dokkoi.gun.GunStatus;
import org.meyason.dokkoi.gun.constants.GunType;
import org.meyason.dokkoi.item.gunitem.GunItem;

public class GunShootEvent{

    public static void onGunShoot(PlayerInteractEvent event, String gunSerial, GunItem gun){
        Game game = Game.getInstance();
        Player player = event.getPlayer();

        GameStatesManager manager = game.getGameStatesManager();

        if(!manager.isExistGunFromSerial(gunSerial)){
            manager.registerGun(gunSerial, gun);
        }

        GunStatus gunStatus = manager.getGunStatusFromSerial(gunSerial);

        event.setCancelled(true);
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

            // 射撃タスクがまだなければ作成
            if(!manager.isOnShootingGunTask(gunSerial)){

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
                            // 弾切れ時は射撃停止タイマーを削除して停止
                            if(manager.hasShootingStopTask(gunSerial)){
                                BukkitRunnable stopTask = manager.getShootingStopTask(gunSerial);
                                if(stopTask != null){
                                    stopTask.cancel();
                                }
                                manager.removeShootingStopTask(gunSerial);
                            }
                        }
                    }
                };
                shootingTask.runTaskTimer(Dokkoi.getInstance(), 0, gun.getFireRate());
                manager.addShootingGunTask(gunSerial, shootingTask);
            }

            // 既存の射撃停止タイマーがあればキャンセル（フラグ延長のため）
            if(manager.hasShootingStopTask(gunSerial)){
                BukkitRunnable oldStopTask = manager.getShootingStopTask(gunSerial);
                if(oldStopTask != null){
                    oldStopTask.cancel();
                }
                manager.removeShootingStopTask(gunSerial);
            }

            // 4tick後に自動で射撃停止タイマーを削除するタスクを作成
            BukkitRunnable stopTask = new BukkitRunnable() {
                @Override
                public void run() {
                    manager.removeShootingStopTask(gunSerial);
                    // タスクが削除されると、次のshootingTask実行時に自動停止される
                }
            };
            stopTask.runTaskLater(Dokkoi.getInstance(), 4);
            manager.addShootingStopTask(gunSerial, stopTask);

        }else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
            if(manager.hasShootingStopTask(gunSerial)) return;
            if (!manager.isOnReloading(player.getUniqueId())) {
                startReload(player, gun, gunStatus);
            }
        }
    }

    private static void startReload(Player player, GunItem gun, GunStatus gunStatus) {
        if (gunStatus.getIsReloading()) {
            return;
        }
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        GunType gunType = gun.getGunType();
        int reloadTime = gun.getReloadTime();

        if(gunStatus.getMagazineAmmo() == gun.getMagazineSize()) return;

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
}
