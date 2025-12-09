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

public class SummonersBrave extends CustomItem {

    public static final String id = "summoners_brave";

    private Game game;
    private Player player;

    public SummonersBrave() {
        super(id, "§a儀礼剣", ItemStack.of(Material.IRON_SWORD), 1);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5とある魔女が地下室から取ってきた剣。"),
                Component.text("§5クッソ寒かった。いや、マジで。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5攻撃力4、インベントリに存在している間、死亡した人間のチャットが見えるようになる。"),
                Component.text("§5内藤に対しては固定670ダメージを与える。")
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
                        new NamespacedKey(JavaPlugin.getProvidingPlugin(getClass()), "Summoners_brave_attack_damage"),
                        4.0,
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
        player.sendMessage(Component.text("§aレイピア§bを手に入れた！"));
    }

}
