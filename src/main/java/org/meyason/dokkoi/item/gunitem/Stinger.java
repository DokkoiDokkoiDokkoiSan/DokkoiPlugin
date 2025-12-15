package org.meyason.dokkoi.item.gunitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.gun.constants.GunType;

import java.util.List;

public class Stinger extends GunItem{

    public static final String id = "stinger";

    public Stinger() {
        super(id, "§aスティンガー", ItemStack.of(Material.STONE_HOE), 1);
        this.gunType = GunType.SMG;
        this.magazineSize = 20;
        this.reloadTime = 2250;
        this.baseDamage = 1.0D;
        this.isExplosive = false;
        this.fireRate = 1;
        this.spread = 0.6D;
        this.bulletSpeed = 3.0D;
        this.shotSound = Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR;
        this.volume = 3.0F;
        this.pitch = 0.8F;
        this.price = 17;
        List<Component> lore = List.of(
                Component.text("§7発射レートがとても高いSMG。"),
                Component.text("§7走りながら撃つのめっちゃ強そうだよね。")
        );
        this.descriptions = lore;
    }
}
