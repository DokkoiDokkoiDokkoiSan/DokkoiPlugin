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

public class HGMagazine extends CustomItem {

    public static final String id = "hg_magazine";

    public HGMagazine() {
        super(id, "§aピストルのマガジン", ItemStack.of(Material.COPPER_INGOT), 64);
        List<Component> lore = List.of(
                Component.text("§5ピストルで使用するマガジン。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5右クリックすることでピストルの弾が10発手に入る。")
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
        manager.setHGInventoryAmmo(player.getUniqueId(), nowAmmo + 10);
        ItemStack item = player.getInventory().getItemInMainHand();
        item.setAmount(item.getAmount() - 1);
    }
}
