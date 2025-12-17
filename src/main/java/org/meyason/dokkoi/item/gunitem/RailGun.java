package org.meyason.dokkoi.item.gunitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.gun.constants.GunType;

import java.util.List;

public class RailGun extends GunItem{

    public static final String id = "rail_gun";

    public RailGun() {
        super(id, "§aRail Gun", ItemStack.of(Material.DIAMOND_HOE), 1);
        this.gunType = GunType.EXPLOSIVE;
        this.magazineSize = 1;
        this.reloadTime = 10000;
        this.baseDamage = 25.0D;
        this.isExplosive = false;
        this.fireRate = 10;
        this.spread = 0.0D;
        this.bulletSpeed = 10.0D;
        this.shotSound = Sound.ENTITY_IRON_GOLEM_DAMAGE;
        this.volume = 10.0F;
        this.pitch = 0.8F;
        this.price = 23;
        List<Component> lore = List.of(
                Component.text("§7凄まじい威力を出すエネルギーガン。"),
                Component.text("§7因みに金効率は悪いらしい。"),
                Component.text("§c一度撃つと壊れる。")
        );
        this.descriptions = lore;
    }
}
