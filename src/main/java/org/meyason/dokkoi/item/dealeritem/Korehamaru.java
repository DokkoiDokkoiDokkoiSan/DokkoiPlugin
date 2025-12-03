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

public class Korehamaru extends CustomItem {

    public static final String id = "korehamaru";

    public Korehamaru() {
        super(id, "§9コレハマール", ItemStack.of(Material.MELON_SEEDS), 64);
        List<Component> lore = List.of(
                Component.text("§5中毒性が高い気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5薬売師以外が所持すると、移動速度低下Lv1を付与され、最大体力が20固定になる。"),
                Component.text("§5捨てることが出来ない。")
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

    public static void activate(Player player) {
        player.sendMessage(Component.text("§c頭がふらふらしてきた..."));
        player.setMaxHealth(20.0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1));
    }
}
