package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class Katakunaru extends CustomItem {

    public static final String id = "katakunaru";

    public Katakunaru() {
        super(id, "§dカタクナール", ItemStack.of(Material.MELON_SEEDS), 64);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5体が丈夫になる気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5次に受けるダメージを無効化する。"),
                Component.text("§5効果は累積しない。")
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

    public static void activate(Player player, ItemStack item){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        if(manager.getIsDeactivateDamageOnce().get(player)){
            player.sendMessage(Component.text("§cすでにダメージ無効化のバフを所持しています。"));
            return;
        }
        manager.addIsDeactivateDamageOnce(player, true);
        item.setAmount(item.getAmount() - 1);
    }
}
