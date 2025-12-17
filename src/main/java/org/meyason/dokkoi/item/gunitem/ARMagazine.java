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

public class ARMagazine extends CustomItem {

    public static final String id = "ar_magazine";

    public ARMagazine() {
        super(id, "§aARのマガジン", ItemStack.of(Material.NETHERITE_INGOT), 64);
        List<Component> lore = List.of(
                Component.text("§5ARのリロードで使用するマガジン。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5右クリックすることでアサルトライフル系の弾が50発手に入る。")
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
        manager.setARInventoryAmmo(player.getUniqueId(), nowAmmo + 50);
        ItemStack item = player.getInventory().getItemInMainHand();
        item.setAmount(item.getAmount() - 1);
    }
}
