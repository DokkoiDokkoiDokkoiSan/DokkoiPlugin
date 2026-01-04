package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;
import org.meyason.dokkoi.item.itemhooker.InventoryHooker;

import java.util.List;

public class Tsuyokunaru extends CustomItem implements InteractHooker {

    public static final String id = "tsuyokunaru";

    public Tsuyokunaru() {
        super(id, "§9ツヨクナール", ItemStack.of(Material.MELON_SEEDS), 64);
        List<Component> lore = List.of(
                Component.text("§5力が強くなる気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§51秒間攻撃の与ダメージが2増える。")
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

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Game.getInstance().getGameStatesManager().addIsDeactivateDamageOnce(player.getUniqueId(), true);
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        manager.addAdditionalDamage(player.getUniqueId(), 2);
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(Component.text("§aちょっと固くなった気がする！"));
        new BukkitRunnable() {

            @Override
            public void run() {
                player.sendMessage(Component.text("§aツヨクナール§bの効果が切れた。"));
                manager.addAdditionalDamage(player.getUniqueId(), -2);

            }
        }.runTaskLater(Dokkoi.getInstance(), 25L);
    }
}
