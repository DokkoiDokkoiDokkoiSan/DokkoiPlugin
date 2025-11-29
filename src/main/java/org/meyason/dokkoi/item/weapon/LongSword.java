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
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class LongSword extends CustomItem {

    public static final String id = "long_sword";

    public Game game;
    private Player player;

    public LongSword(){
        super(id, "§aロングソード", ItemStack.of(Material.STONE_SWORD), 1);
        List<Component> lore = List.of(
                Component.text("§5割と安い剣、確か350円くらいで買った。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5攻撃力3")
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
                        new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "long_sword_attack_damage"),
                        3.0,
                        AttributeModifier.Operation.ADD_NUMBER
                );
                meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
                item.setItemMeta(meta);
            }
            return item;
        };
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        player.sendMessage(Component.text("§aロングソード§bを手に入れた！"));
    }

}
