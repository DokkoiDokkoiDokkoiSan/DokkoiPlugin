package org.meyason.dokkoi.item.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.util.ArrayList;
import java.util.List;

public class Tier3PlayerList extends CustomItem {

    public static final String id = "tier_3_player_list";

    private Player player;
    private Game game;

    public Tier3PlayerList() {
        super(id, "魔女図鑑", ItemStack.of(Material.PAPER));
        this.baseItem = ItemStack.of(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) baseItem.getItemMeta();
        bookMeta.setTitle("§6魔女図鑑");
        bookMeta.setAuthor("Dokkoi");
        List<Component> lore = List.of(
                Component.text("§7tier3勝利条件を選んだプレイヤーの名前が記入されている。名前の割に魔女の情報は一つも書いてない。"),
                Component.text("§7本を開くとtier3勝利条件を選んだプレイヤーの名前が記入されている。"),
                Component.text("§bこれらのプレイヤーをすべて殺害せよ。")
        );
        bookMeta.lore(lore);
        baseItem.setItemMeta(bookMeta);
        isUnique = true;
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
        player.sendMessage(Component.text("§6魔女図鑑§bを手に入れた！"));
        game.getGameStatesManager().setEnableKillerList(true);
    }

    public void updateList(){
        this.player.getInventory().removeItem(this.baseItem);
        ItemStack book = baseItem.clone();
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        List<Player> players = new ArrayList<>(game.getGameStatesManager().getAlivePlayers());
        StringBuilder names = new StringBuilder();
        for(Player p : players){
            if(game.getGameStatesManager().getPlayerGoals().get(p).tier == Tier.TIER_3) {
                names.append("§2- ").append(p.getName()).append("\n");
            }
        }
        bookMeta.setPages(names.toString());
        book.setItemMeta(bookMeta);
        this.baseItem = book;
        //アイテム更新
        this.player.getInventory().addItem(book);
    }
}
