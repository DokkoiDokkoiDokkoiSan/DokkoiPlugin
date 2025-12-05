package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class TotemoKizukieru extends CustomItem {

    public static final String id = "totemo_kizukieru";

    public TotemoKizukieru() {
        super(id, "§6トテモツヨクナール", ItemStack.of(Material.FROGSPAWN), 16);
        List<Component> lore = List.of(
                Component.text("§5かなり傷が治る気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5再生Lv3を常時受け取る。")
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 3, false, false, true));

        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(Component.text("§aめっちゃ傷が治る気がする！"));
    }

}
