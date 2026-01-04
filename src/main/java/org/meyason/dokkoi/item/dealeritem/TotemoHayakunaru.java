package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.units.qual.C;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;

import java.util.List;

public class TotemoHayakunaru extends CustomItem implements InteractHooker {

    public static final String id = "totemo_hayakunaru";

    public TotemoHayakunaru() {
        super(id, "§6トテモハヤクナール", ItemStack.of(Material.FROGSPAWN), 16);
        List<Component> lore = List.of(
                Component.text("§5かなり足が速くなる気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5移動速度Lv2を常時受け取る。")
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

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false, true));

        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(Component.text("§aめっちゃ足が速くなった気がする！"));
    }
}
