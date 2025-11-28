package org.meyason.dokkoi.item.jobitem.gacha;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class StrongestStrongestStrongestBall extends CustomItem {

    private Game game;
    private Player player;

    public static final String id = "strongest_strongest_strongest_ball";

    public StrongestStrongestStrongestBall() {
        super(id, "§aガチで最強のたまたま", ItemStack.of(Material.FIRE_CHARGE), 64);
        List<Component> lore = List.of(
                Component.text("§5最強のおじさんから摘出したたまたまを全力で磨いたもの。ただならぬオーラを感じる。臭い。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5これ引いたら勝ちだしこれいる？")
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
        player.sendMessage(Component.text("§6ガチで最強のたまたま§bを手に入れた！"));
    }
}
