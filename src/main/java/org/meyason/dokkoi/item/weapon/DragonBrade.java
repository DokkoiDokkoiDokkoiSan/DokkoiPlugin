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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class DragonBrade extends CustomItem {

    public static final String id = "dragon_brade";

    public DragonBrade() {
        super(id, "§a龍一文字", ItemStack.of(Material.IRON_SWORD), 1);
        List<Component> lore = List.of(
                Component.text("§5体竜神の意思が宿ってそうな剣。"),
                Component.text("§6ﾁｭｲｰﾝwwwwﾁｭｲｰﾝwwwwwwﾁｭｲﾝﾁｭｲﾝwwwﾁｭｲｰﾝwwwwﾁｭｲｰﾝwwwwwwﾁｭｲﾝﾁｭｲﾝwwwｳﾞｫﾝwwwwwｳﾞｫﾝwwwwｳﾞｫﾁｭｲｰﾝwwwwww"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5攻撃力5"),
                Component.text("§5手に持っている間移動速度上昇Lv1とジャンプ力上昇Lv1を得る。")
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, Integer.MAX_VALUE, 1, false, false, true));
    }

    public static void deactivate(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
    }
}
