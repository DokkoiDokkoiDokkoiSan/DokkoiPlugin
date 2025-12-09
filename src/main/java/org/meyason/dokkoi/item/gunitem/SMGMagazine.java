package org.meyason.dokkoi.item.gunitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class SMGMagazine extends CustomItem {

    public static final String id = "smg_magazine";

    public SMGMagazine() {
        super(id, "§aSMGのマガジン", ItemStack.of(Material.IRON_INGOT), 64);
        List<Component> lore = List.of(
                Component.text("§5SMGのリロードで使用するマガジン。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5右クリックすることでサブマシンガン系の弾が40発手に入る。")
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

    public static void activate(Player player){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        long nowAmmo = manager.getHGInventoryAmmo(player.getUniqueId());
        manager.setHGInventoryAmmo(player.getUniqueId(), nowAmmo + 15);
        ItemStack item = player.getInventory().getItemInMainHand();
        item.setAmount(item.getAmount() - 1);
    }
}
