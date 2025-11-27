package org.meyason.dokkoi.item.jobitem.gacha;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class StrongestBall extends CustomItem {

    private Game game;
    private Player player;

    public static final String id = "strongest_ball";

    public StrongestBall() {
        super(id, "§a最強のたまたま", ItemStack.of(Material.FIRE_CHARGE), 64);
        List<Component> lore = List.of(
                Component.text("§5最強のおじさんから摘出したたまたま。なんかめっちゃ黒い。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5インベントリに存在している間、受けるダメージを70％の確率で無効化する。")
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

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        player.sendMessage(Component.text("§a最強のたまたま§bを手に入れた！"));
    }
}
