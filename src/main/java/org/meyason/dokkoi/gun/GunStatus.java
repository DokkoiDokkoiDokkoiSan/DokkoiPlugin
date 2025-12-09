package org.meyason.dokkoi.gun;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.gun.constants.GunType;
import org.meyason.dokkoi.item.gunitem.GunItem;

public class GunStatus {

    private GunItem gun;

    private long inventoryAmmo;

    private int magazineAmmo;

    private boolean isReloading;

    private long reloadEndTime;

    private GameStatesManager manager;

    public GunStatus(GunItem gun) {
        this.gun = gun;
        this.magazineAmmo = gun.getMagazineSize();
        this.manager = Game.getInstance().getGameStatesManager();
    }

    public GunItem getGun(){
        return this.gun;
    }

    public void setInventoryAmmo(GunType gunType, Player player, long inventoryAmmo){
        switch (gunType){
            case HG:
                manager.setHGInventoryAmmo(player.getUniqueId(), inventoryAmmo);
                break;
            case SMG:
                manager.setSMGInventoryAmmo(player.getUniqueId(), inventoryAmmo);
                break;
        }
    }

    public long getInventoryAmmo(GunType gunType, Player player){
        switch (gunType){
            case HG:
                return manager.getHGInventoryAmmo(player.getUniqueId());
            case SMG:
                return manager.getSMGInventoryAmmo(player.getUniqueId());
        }
        return 0;
    }

    public int getMagazineAmmo(){
        return this.magazineAmmo;
    }

    public boolean shoot() {
        if (this.magazineAmmo <= 0 || this.isReloading) {
            return false;
        }
        this.magazineAmmo--;
        return true;
    }

    public boolean startReload(GunType gunType, int reloadTime, Player player){
        this.inventoryAmmo = getInventoryAmmo(gunType, player);
        if(inventoryAmmo <= 0){
            return false;
        }
        this.isReloading = true;
        this.reloadEndTime = System.currentTimeMillis() + reloadTime;
        return true;
    }

    public void finishReload(GunType gunType, Player player){
        this.isReloading = false;
        long nowAllAmmo = getInventoryAmmo(gunType, player) + (long)magazineAmmo;
        setInventoryAmmo(gunType, player, nowAllAmmo);
        if(nowAllAmmo <= gun.getMagazineSize()){
            this.magazineAmmo = (int) nowAllAmmo;
            setInventoryAmmo(gunType, player, 0);
        } else {
            setInventoryAmmo(gunType, player, nowAllAmmo - (long)(gun.getMagazineSize()));
            this.magazineAmmo = gun.getMagazineSize();
        }
        this.inventoryAmmo = getInventoryAmmo(gunType, player);
        updateActionBar(player);
    }

    public void updateAmmo(GunType gunType, Player player){
        this.inventoryAmmo = getInventoryAmmo(gunType, player);
        updateActionBar(player);
    }

    public String getAmmoDisplay() {
        if (isReloading) {
            long remainingTime = getReloadRemainingTime();
            double seconds = remainingTime / 1000.0;
            return String.format("<< Reload %.1f >>", seconds);
        }
        return String.format("<< %d/%d >>", magazineAmmo, this.inventoryAmmo);
    }

    public void cancelReload() {
        this.isReloading = false;
    }

    public boolean getIsReloading(){
        return this.isReloading;
    }

    public long getReloadRemainingTime() {
        return Math.max(0, reloadEndTime - System.currentTimeMillis());
    }

    public void updateActionBar(Player player) {
        String ammoDisplay = getAmmoDisplay();
        Component message = Component.text(ammoDisplay);

        if (isReloading) {
            message = message.color(NamedTextColor.RED);
        }

        player.sendActionBar(message);
    }

}
