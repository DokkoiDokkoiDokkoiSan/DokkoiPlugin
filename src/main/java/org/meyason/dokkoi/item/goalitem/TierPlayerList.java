package org.meyason.dokkoi.item.goalitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

import java.util.ArrayList;
import java.util.List;

public class TierPlayerList extends CustomItem {

    public static final String id = "tier_player_list";

    private Player player;
    private Game game;

    private List<Player> targetPlayers = new ArrayList<>();

    public TierPlayerList() {
        super(id, "§a魔女図鑑", ItemStack.of(Material.PAPER), 1);
        this.baseItem = ItemStack.of(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) baseItem.getItemMeta();
        bookMeta.setTitle("§a魔女図鑑");
        bookMeta.setAuthor("§6二階堂真紅");
        List<Component> lore = List.of(
                Component.text("§5名前の割に魔女の情報は一つも書いてない。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5一番選択された数が多いtierの勝利条件を選んだプレイヤーの名前が記入されている。"),
                Component.text("§5本を開くとその勝利条件を選んだプレイヤーの名前が記入されている。"),
                Component.text("§bこれらのプレイヤーをすべて殺害せよ。")
        );
        bookMeta.lore(lore);
        setDescription(lore);
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
        player.sendMessage(Component.text("§a魔女図鑑§bを手に入れた！"));
    }

    public void updateList(Tier targetTier){
        ItemStack book = this.baseItem.clone();
        GameItem.removeItem(player, GameItemKeyString.TIERPLAYERLIST, 1);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        List<Player> players = new ArrayList<>(game.getGameStatesManager().getAlivePlayers());
        StringBuilder names = new StringBuilder();
        for(Player p : players){
            if(game.getGameStatesManager().getPlayerGoals().get(p).tier == targetTier) {
                names.append("§2- ").append(p.getName()).append("\n");
                targetPlayers.add(p);
            }
        }
        bookMeta.setPages(names.toString());
        book.setItemMeta(bookMeta);
        this.baseItem = book;
        //アイテム更新
        this.player.getInventory().addItem(book);
    }

    public List<Player> getTargetPlayers(){
        return targetPlayers;
    }
}
