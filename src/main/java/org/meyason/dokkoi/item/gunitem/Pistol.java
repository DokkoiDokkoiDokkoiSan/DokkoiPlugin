package org.meyason.dokkoi.item.gunitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.gun.constants.GunType;

import java.util.List;


public class Pistol extends GunItem {

    public static final String id = "pistol";

    public Pistol() {
        super("pistol", "§aピストル", ItemStack.of(Material.WOODEN_HOE), 1);
        this.gunType = GunType.HG;
        this.magazineSize = 10;
        this.reloadTime = 1500;
        this.baseDamage = 3.0D;
        this.isExplosive = false;
        this.fireRate = 10;
        this.spread = 0.15D;
        this.bulletSpeed = 5.0D;
        this.shotSound = Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR;
        this.volume = 5.0F;
        this.pitch = 2.0F;
        this.price = 11;
        List<Component> lore = List.of(
                Component.text("§7初期配布されてそうなピストル。めっちゃお金稼げそう。")
        );
        this.descriptions = lore;
    }
}
