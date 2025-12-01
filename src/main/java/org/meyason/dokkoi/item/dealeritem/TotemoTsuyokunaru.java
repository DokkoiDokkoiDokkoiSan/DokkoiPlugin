package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class TotemoTsuyokunaru extends CustomItem {

    public static final String id = "totemo_tsuyokunaru";

    public TotemoTsuyokunaru() {
        super(id, "§6トテモツヨクナール", ItemStack.of(Material.FROGSPAWN), 16);
        List<Component> lore = List.of(
                Component.text("§5かなり力が強くなる気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5与ダメージ固定3増加を常時受け取る。")
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

    public static void activate(Player player, ItemStack item) {
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        manager.addAdditionalDamage(player.getUniqueId(), 2);
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(Component.text("§aトテモツヨクナール§bの効果で攻撃力が上がった！"));
    }
}
