package org.meyason.dokkoi.item.gunitem;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.gun.constants.GunType;

public class Stinger extends GunItem{

    public static final String id = "stinger";

    public Stinger() {
        super(id, "§cスティンガー", ItemStack.of(Material.STONE_HOE), 1);
        this.gunType = GunType.SMG;

    }
}
