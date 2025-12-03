package org.meyason.dokkoi.item.goalitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnkillerList extends CustomItem {

    public static final String id = "unkiller_list";


    private Player player;
    private Game game;

    private List<UUID> targetPlayerList = new ArrayList<>();

    public UnkillerList() {
        super(id, "§a殺してないノート", ItemStack.of(Material.WRITTEN_BOOK), 1);
        isUnique = true;
    }


    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                BookMeta bookMeta = (BookMeta) item.getItemMeta();
                bookMeta.setTitle("§a殺してないノート");
                bookMeta.setAuthor("§6二階堂真紅");
                List<Component> lore = List.of(
                        Component.text("§5なんかでかいムカデが落としたメモ帳。勝手に文字消えてく、こわ。"),
                        Component.text(""),
                        Component.text("§b効果"),
                        Component.text("§5本を開くと、まだ殺人をしていないプレイヤー名が記入されている。"),
                        Component.text("§5また、ノートを左クリックすることで記入されたプレイヤーに発光を10秒間付与する。"),
                        Component.text("§cCT 30秒"),
                        Component.text("§bこれらのプレイヤーをすべて殺害せよ。")
                );
                List<UUID> alivePlayers = new ArrayList<>(Game.getInstance().getGameStatesManager().getAlivePlayers().stream().toList());
                this.targetPlayerList = alivePlayers;
                StringBuilder names = new StringBuilder();
                for(UUID id : alivePlayers){
                    Player p = Bukkit.getPlayer(id);
                    if(p == null) continue;
                    names.append("§2- ").append(p.getName()).append("\n");
                }
                bookMeta.setPages(names.toString());
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
        player.sendMessage(Component.text("§a殺してないノート§bを手に入れた！"));
        game.getGameStatesManager().setEnableKillerList(true);
    }

    public void updateUnKillerList(){
        ItemStack book = baseItem.clone();
        GameItem.removeItem(player, UnkillerList.id, 1);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        List<UUID> killerPlayers = new ArrayList<>(game.getGameStatesManager().getKillerList().keySet());
        List<UUID> alivePlayers = new ArrayList<>(game.getGameStatesManager().getAlivePlayers().stream().toList());
        List<UUID> unKillerPlayers = new ArrayList<>();
        for(UUID id : alivePlayers){
            if(!killerPlayers.contains(id)){
                unKillerPlayers.add(id);
            }
        }
        StringBuilder names = new StringBuilder();
        for(UUID id : unKillerPlayers){
            Player p = Bukkit.getPlayer(id);
            if(p == null) continue;
            names.append("§2- ").append(p.getName()).append("\n");
        }
        bookMeta.setPages(names.toString());
        book.setItemMeta(bookMeta);
        this.baseItem = book;
        //アイテム更新
        this.player.getInventory().addItem(book);
        this.targetPlayerList = unKillerPlayers;
    }

    public void skill(GameStatesManager gameStatesManager, Player owner){
        UUID uuid = owner.getUniqueId();
        if(gameStatesManager.getItemCoolDownScheduler().containsKey(uuid)){
            owner.sendMessage("§cクールタイム中です");
            return;
        }
        for(UUID id : this.targetPlayerList){
            Player p = Bukkit.getPlayer(id);
            if(p == null) continue;
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10 * 20, 1));
        }
        BukkitRunnable itemInitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(gameStatesManager.getGameState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }
                if (!owner.isOnline() || !gameStatesManager.getItemCoolDownScheduler().containsKey(uuid)) {
                    gameStatesManager.removeItemCoolDownScheduler(uuid);
                    cancel();
                    return;
                }
                gameStatesManager.removeItemCoolDownScheduler(uuid);
            }
        };
        itemInitTask.runTaskLater(Dokkoi.getInstance(), 30 * 20L);
        gameStatesManager.addItemCoolDownScheduler(uuid, itemInitTask);
        owner.sendMessage("§aひよってる奴らの身体が光りだした！");
    }

    public List<UUID> getTargetPlayerList() {
        return targetPlayerList;
    }

}
