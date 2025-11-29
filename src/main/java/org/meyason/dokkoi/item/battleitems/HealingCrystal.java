package org.meyason.dokkoi.item.battleitems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import net.kyori.adventure.text.Component;

import java.util.List;

public class HealingCrystal extends CustomItem {

    private Game game;
    private Player player;

    public static final String id = "healingcrystal";

    public HealingCrystal() {
        super(id,"§a回復結晶§r", ItemStack.of(Material.END_CRYSTAL),64);
        List<Component> lore = List.of(
                Component.text("§5回復力が強そうな結晶。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5使用すると体力を5回復する。")
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
    }
}