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

public class Hayakunaru extends CustomItem {

    public static final String id = "hayakunaru";

    public Hayakunaru() {
        super(id, "§9ハヤクナール", ItemStack.of(Material.MELON_SEEDS), 64);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5足が速くなる気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§55秒間移動速度増加Lv2を受け取る。")
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
        item.setAmount(item.getAmount() - 1);
        player.sendMessage(Component.text("§aハヤクナール§bの効果で移動速度が上がった！"));
        int duration = 5*20;
        int amplifier = 2;
        PotionEffect speedEffect = player.getPotionEffect(PotionEffectType.SPEED);
        if (speedEffect == null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
            return;
        }
        if(speedEffect.getAmplifier() > amplifier){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedEffect.getDuration() + (duration/2), speedEffect.getAmplifier()));
        }else if(speedEffect.getAmplifier() == amplifier){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedEffect.getDuration() + duration, speedEffect.getAmplifier()));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speedEffect.getDuration() + duration, amplifier));
        }
    }
}
