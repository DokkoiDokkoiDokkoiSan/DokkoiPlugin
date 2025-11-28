package org.meyason.dokkoi.item.goalitem;

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
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

import java.util.ArrayList;
import java.util.List;

public class KillerList extends CustomItem {

    public static final String id = "killer_list";

    private Player player;
    private Game game;

    private List<Player> targetPlayerList = new ArrayList<>();

    public KillerList() {
        super(id, "§a殺すノート", ItemStack.of(Material.WRITTEN_BOOK), 1);
        isUnique = true;
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                BookMeta bookMeta = (BookMeta) item.getItemMeta();
                bookMeta.setTitle("§a殺すノート");
                bookMeta.setAuthor("§6二階堂真紅");
                List<Component> lore = List.of(
                        Component.text("§5なんかでかいトカゲが落としたメモ帳。勝手に文字書かれる、こわ。"),
                        Component.text(""),
                        Component.text("§b効果"),
                        Component.text("§5殺人をしたプレイヤー名が自身のチャットログにアナウンスされ、ノートに記入される。"),
                        Component.text("§5また、ノートを左クリックすることで記入されたプレイヤーに発光を10秒間付与する。"),
                        Component.text("§cCT 30秒"),
                        Component.text("§bこれらのプレイヤーをすべて殺害せよ。")
                );
                bookMeta.lore(lore);
                setDescription(lore);
                item.setItemMeta(bookMeta);
            }
            return item;
        };
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        player.sendMessage(Component.text("§a殺すノート§bを手に入れた！"));
        game.getGameStatesManager().setEnableKillerList(true);
    }

    public void updateKillerList(){
        ItemStack book = baseItem.clone();
        GameItem.removeItem(player, GameItemKeyString.KILLER_LIST, 1);
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
        this.targetPlayerList = killerPlayers;
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
                if(gameStatesManager.getGameState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }
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

    public List<Player> getTargetPlayerList() {
        return targetPlayerList;
    }
}
