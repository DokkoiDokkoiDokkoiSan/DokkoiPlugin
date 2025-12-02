package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class TotemoKorehamaru extends CustomItem {


    public static final String id = "totemo_korehamaru";

    public TotemoKorehamaru() {
        super(id, "§6トテモコレハマール", ItemStack.of(Material.COCOA_BEANS), 16);
        List<Component> lore = List.of(
                Component.text("§5かなり中毒性が高い気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5薬売師以外のプレイヤーが拾うと死亡する。"),
                Component.text("§5このアイテムでの死亡は、薬売師の殺害判定となる。")
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

}
