package org.meyason.dokkoi.item.battleitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class ArcherArmor extends CustomItem {

    public static final String id = "golden_armor";

    public Game game;
    private Player player;

    public ArcherArmor() {
        super(id, "弓使いの鎧", ItemStack.of(Material.GOLDEN_CHESTPLATE), 1);
        List<Component> lore = List.of(
                Component.text("§5矢を一発だけ防いでくれそうなアーマー。めちゃくちゃ脆い。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5使用すると次に受けるダメージを一度だけ0にする。")
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
        player.sendMessage(Component.text("§aロングソード§bを手に入れた！"));
    }

    public void activate(){
        ItemStack item = player.getInventory().getChestplate();
        if(item != null && item.isSimilar(this.baseItem)){
            player.getInventory().setChestplate(null);
            player.sendMessage(Component.text("§a弓使いの鎧§bの効果が発動した！"));
        }
    }

}
