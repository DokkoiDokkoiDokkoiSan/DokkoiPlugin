package org.meyason.dokkoi.item.jobitem;

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

public class Hikakin extends CustomItem {

    public static final String id = "hikakin";

    private Game game;
    private Player player;

    public Hikakin() {
        super(id, "§a火かき棒", ItemStack.of(Material.STONE_SWORD), 1);
        isUnique = true;
        hasSerialNumber = true;
        List<Component> lore = List.of(
                Component.text("§5持ってたらこの後死ぬ気がする棒。なんか正しくなくね？これ。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5攻撃力2。捨てることが出来ない。")
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
                        new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "Hikakin_attack_damage"),
                        2.0,
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
        player.sendMessage(Component.text("§a火かき棒§bを手に入れた！"));
    }

}
