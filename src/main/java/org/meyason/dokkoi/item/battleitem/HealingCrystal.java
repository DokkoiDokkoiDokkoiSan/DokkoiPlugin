package org.meyason.dokkoi.item.battleitem;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import net.kyori.adventure.text.Component;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;

import java.util.List;

public class HealingCrystal extends CustomItem implements InteractHooker {

    public static final String id = "healing_crystal";

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

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (player.getHealth() == player.getMaxHealth()) {
            player.sendActionBar(Component.text("§c既に最大体力です。"));
            return;
        }
        double newHealth = player.getHealth() + 5.0;
        if (newHealth > player.getMaxHealth()) {
            newHealth = player.getMaxHealth();
        }
        player.setHealth(newHealth);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10, 1);
        player.sendMessage("§a回復結晶§でHPを回復した。");

        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item);
    }
}
