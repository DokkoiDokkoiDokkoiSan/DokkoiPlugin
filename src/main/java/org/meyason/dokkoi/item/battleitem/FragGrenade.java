package org.meyason.dokkoi.item.battleitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class FragGrenade extends CustomItem {

    public static final String id = "frag_grenade";

    public FragGrenade() {
        super(id, "§aフラググレネード", ItemStack.of(Material.EGG), 64);
        List<Component> lore = List.of(
                Component.text("§5投げると時間を置いて爆発するグレネード。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5グレネードは投げてから5秒後に爆発する。"),
                Component.text("§5爆発は半径7m以内のプレイヤーに固定20ダメージを与える。"),
                Component.text("§5投げたグレネードが地面にぶつかるとその場に留まる。")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                item.setItemMeta(meta);
            }
            return item;
        };
    }

    public static void throwGrenade(){

    }

    public static void explodeGrenade(){

    }
}
