package org.meyason.dokkoi.item.gunitem;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.gun.constants.GunType;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public abstract class GunItem extends CustomItem {

    protected GunType gunType;

    protected int magazineSize;

    protected double baseDamage;

    protected boolean isExplosive;

    protected float explosionRadius;

    protected int fireRate;

    protected int reloadTime;

    protected double spread;

    protected double bulletSpeed;

    protected Sound shotSound;
    protected float volume;
    protected float pitch;

    protected List<Component> descriptions;

    protected int price;

    public GunItem(String id, String name, ItemStack baseItem, int maxStackSize) {
        super(id, name, baseItem, 1);
        isGun = true;
    }

    public GunType getGunType() {
        return gunType;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public boolean isExplosive() {
        return isExplosive;
    }

    public float getExplosionRadius() {
        return explosionRadius;
    }

    public int getFireRate() {
        return fireRate;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public double getSpread() {
        return spread;
    }

    public double getBulletSpeed() {
        return bulletSpeed;
    }

    public Sound getShotSound() {
        return shotSound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public List<Component> getDescriptions() {
        return descriptions;
    }

    public int getPrice() {
        return price;
    }

    public @NotNull List<Component> getGunComponents() {
        String category = "§7カテゴリ: §b";
        switch (gunType){
            case AR:
                category += "アサルトライフル";
                break;
            case SMG:
                category += "サブマシンガン";
                break;
            case HG:
                category += "ハンドガン";
                break;
            case EXPLOSIVE:
                category += "爆発系";
                break;
        }
        List<Component> lore = new java.util.ArrayList<>(List.of(
                Component.text(category)
        ));
        lore.addAll(descriptions);
        lore.add(Component.text(" "));
        lore.add(Component.text("§7マガジンサイズ: §b" + magazineSize));
        lore.add(Component.text("§7リロード速度: §b" + reloadTime/1000 + "秒"));

        return lore;
    }

    @Override
    protected void registerItemFunction() {
        NamespacedKey serialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.GUN_SERIAL);
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                item.setItemMeta(meta);
                String serial = meta.getPersistentDataContainer().get(serialKey, PersistentDataType.STRING);
                Game.getInstance().getGameStatesManager().registerGun(serial, this);
            }
            return item;
        };
    }
}
