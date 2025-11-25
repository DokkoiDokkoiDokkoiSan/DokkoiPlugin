package org.meyason.dokkoi.item.battleitems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.awt.*;

public class HealingCristal extends CustomItem {

    private Game game;
    private Player player;

    public static final String id = "HealingCristal";

    public HealingCristal() {
        super(id,"§a回復結晶§r", ItemStack.of(Material.END_CRYSTAL),64);
        List<Component> lore = List.of(
                Component.text()
        )
    }
}
