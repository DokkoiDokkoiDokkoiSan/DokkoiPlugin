package org.meyason.dokkoi.item.weapon;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class DrainBrade extends CustomItem {

    public static final String id = "drain_brade";

    public DrainBrade() {
        super(id, "§aドランブレード", ItemStack.of(Material.IRON_SWORD), 1);
        List<Component> lore = List.of(
                Component.text("§5体力吸収しそうな剣。"),
                Component.text("§5あんまり早く売らない方がいい気がする。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5攻撃力5"),
                Component.text("§5プレイヤーやモブを斬ったとき、自身のHPを1回復する。")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                meta.removeAttributeModifier(Attribute.ATTACK_DAMAGE);
                AttributeModifier modifier = new AttributeModifier(
                        new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), id),
                        5.0,
                        AttributeModifier.Operation.ADD_NUMBER
                );
                meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
                item.setItemMeta(meta);
            }
            return item;
        };
    }

    public static void activate(Player player) {
        player.setHealth(Math.min(player.getHealth() + 1.0, player.getMaxHealth()));
    }
}
