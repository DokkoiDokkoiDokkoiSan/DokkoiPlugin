package org.meyason.dokkoi.item.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KillerList extends CustomItem {

    public static final String id = "killer_list";

    private Player player;
    private Game game;

    public KillerList() {
        super(id, "殺すノート", ItemStack.of(Material.PAPER));
        this.baseItem = ItemStack.of(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) baseItem.getItemMeta();
        bookMeta.setTitle("§6殺すノート");
        bookMeta.setAuthor("Dokkoi");
        List<Component> lore = List.of(
                Component.text("§7殺人をしたプレイヤー名が自身のチャットログにアナウンスされ、ノートに記入される。"),
                Component.text("§7また、ノートを左クリックすることで記入されたプレイヤーに発光を10秒間付与する。"),
                Component.text("§cCT 30秒\n"),
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
        player.sendMessage(Component.text("§6殺すノート§bを手に入れた！"));
        game.getGameStatesManager().setEnableKillerList(true);
    }

    public void updateKillerList(){
        this.player.getInventory().removeItem(this.baseItem);
        ItemStack book = baseItem.clone();
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        List<Player> killerPlayers = new ArrayList<>(game.getGameStatesManager().getKillerList().keySet());
        StringBuilder names = new StringBuilder();
        for(Player p : killerPlayers){
            if(!game.getGameStatesManager().getAlivePlayers().contains(p)){
                names.append("§4§m- ").append(p.getName()).append("\n");
            }
            names.append("§2- ").append(p.getName()).append("\n");
        }
        bookMeta.setPages(names.toString());
        book.setItemMeta(bookMeta);
        this.baseItem = book;
        //アイテム更新
        this.player.getInventory().addItem(book);
    }

    public void skill(GameStatesManager gameStatesManager){
        if(gameStatesManager.getItemCoolDownScheduler().containsKey(player)){
            player.sendMessage("§cクールタイム中です");
            return;
        }
        List<Player> killerList = new ArrayList<>(game.getGameStatesManager().getKillerList().keySet());
        for(Player player : killerList){
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10 * 20, 1));
        }
        BukkitRunnable itemInitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !gameStatesManager.getItemCoolDownScheduler().containsKey(player)) {
                    cancel();
                    return;
                }
                gameStatesManager.removeItemCoolDownScheduler(player);
            }
        };
        itemInitTask.runTaskLater(Dokkoi.getInstance(), 30 * 20L);
        gameStatesManager.addItemCoolDownScheduler(player, itemInitTask);
    }


}
