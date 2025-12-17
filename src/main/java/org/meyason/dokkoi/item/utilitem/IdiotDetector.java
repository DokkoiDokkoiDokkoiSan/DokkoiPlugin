package org.meyason.dokkoi.item.utilitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;
import java.util.UUID;

public class IdiotDetector extends CustomItem {

    public static final String id = "idiot_detector";

    public IdiotDetector() {
        super(id, "§b愚か者探知機", ItemStack.of(Material.SNIFFER_EGG), 1);
        List<Component> lore = List.of(
                Component.text("§5死んでしまった愚か者の名前を見ることができる。"),
                Component.text("§5名前を調べてお空の上にいる皆を煽ることができる。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5使用すると死亡したプレイヤーの名前を知ることができる。")
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

    public static void activate(Player player, ItemStack itemStack){
        Game game = Game.getInstance();
        GameStatesManager manager = game.getGameStatesManager();
        List<UUID> deadPlayers = manager.getKillerList().values().stream().toList();
        if(deadPlayers.isEmpty()){
            player.sendMessage(Component.text("§cまだ誰も死んでいない。"));
        } else {
            player.sendMessage(Component.text("§a---現在の死亡者---"));
            for(UUID uuid : deadPlayers){
                Player deadPlayer = Bukkit.getPlayer(uuid);
                if(deadPlayer != null){
                    player.sendMessage(Component.text("§5" + deadPlayer.getName()));
                }
            }
        }
        itemStack.setAmount(0);
    }
}
