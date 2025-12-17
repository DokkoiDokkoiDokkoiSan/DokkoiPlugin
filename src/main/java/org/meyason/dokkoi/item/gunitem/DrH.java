package org.meyason.dokkoi.item.gunitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.gun.constants.GunType;

import java.util.List;

public class DrH extends GunItem{

    public static final String id = "dr_h";

    public DrH() {
        super(id, "§aDR-H", ItemStack.of(Material.IRON_HOE), 1);
        this.gunType = GunType.AR;
        this.magazineSize = 20;
        this.reloadTime = 2500;
        this.baseDamage = 2.0D;
        this.isExplosive = false;
        this.fireRate = 3;
        this.spread = 0.3D;
        this.bulletSpeed = 7.0D;
        this.shotSound = Sound.BLOCK_PISTON_EXTEND;
        this.volume = 4.0F;
        this.pitch = 1.8F;
        this.price = 23;
        List<Component> lore = List.of(
                Component.text("§7扱いが少し難しいAR。"),
                Component.text("§7このゲームがスマホでも出来るように設計されていたら胴体3発で殺せる性能だったらしい。")
        );
        this.descriptions = lore;
    }
}
