package org.meyason.dokkoi.item.dealeritem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;

import java.util.List;

public class TotemoKatakunaru extends CustomItem implements InteractHooker {

    public static final String id = "totemo_katakunaru";

    public TotemoKatakunaru() {
        super(id, "§6トテモカタクナール", ItemStack.of(Material.FROGSPAWN), 16);
        List<Component> lore = List.of(
                Component.text("§5かなり体が丈夫になる気がする薬。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5状態異常効果を常時受けなくなる。")
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

        Game.getInstance().getGameStatesManager().addOnDisablePotionEffectPlayer(player.getUniqueId());

        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(Component.text("§aめっちゃ固くなった気がする！"));
    }
}
