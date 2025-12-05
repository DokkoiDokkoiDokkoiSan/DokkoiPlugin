package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class Kizukieru extends CustomItem {

    public static final String id = "kizukieru";

    public Kizukieru(){
        super(id, "§9キズキエール", ItemStack.of(Material.MELON_SEEDS), 64);
        List<Component> lore = List.of(
                Component.text("§5傷が治る気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5HPが5回復する。")
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

    public static void activate(Player player, ItemStack item) {

        double nowHealth = player.getHealth();
        if(nowHealth == player.getMaxHealth()){
            player.sendActionBar(Component.text("§c既に最大体力です。"));
            return;
        }else if(nowHealth + 5.0 > player.getMaxHealth()){
            player.setHealth(player.getMaxHealth());
        }else{
            player.setHealth(nowHealth + 5.0);
        }
        item.setAmount(item.getAmount() - 1);
        player.sendMessage(Component.text("§aキズキエール§bでHPを回復した。"));
    }
}
