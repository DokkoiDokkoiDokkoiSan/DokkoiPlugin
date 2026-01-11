package org.meyason.dokkoi.item.goalitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TierPlayerList extends CustomItem implements InteractHooker {

    public static final String id = "tier_player_list";

    private Player player;
    private Game game;

    private List<UUID> targetPlayers = new ArrayList<>();

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
                Component.text("§b本を左クリックで使用すると記入されているプレイヤーに発光を10秒間付与する。捨てることが出来ない。")
        );
        bookMeta.lore(lore);
        setDescription(lore);
        baseItem.setItemMeta(bookMeta);
        isUnique = true;
        hasSerialNumber = true;
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
        GameItem.removeItem(player, TierPlayerList.id, 1);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        List<UUID> playerUUID = new ArrayList<>(game.getGameStatesManager().getAlivePlayers());
        StringBuilder names = new StringBuilder();
        for(UUID uuid : playerUUID){
            Player p = Bukkit.getPlayer(uuid);
            if(p == null) continue;
            if(p.getUniqueId() == player.getUniqueId()) continue;
            if(game.getGameStatesManager().getPlayerGoals().get(p.getUniqueId()).tier == targetTier) {
                names.append("§2- ").append(p.getName()).append("\n");
                targetPlayers.add(p.getUniqueId());
            }
        }
        bookMeta.setPages(names.toString());
        book.setItemMeta(bookMeta);
        this.baseItem = book;
        //アイテム更新
        this.player.getInventory().addItem(book);
    }

    public List<UUID> getTargetPlayers(){
        return targetPlayers;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player owner = event.getPlayer();
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();

        UUID uuid = owner.getUniqueId();
        if(gameStatesManager.getItemCoolDownScheduler().containsKey(uuid)){
            owner.sendMessage("§cクールタイム中です");
            return;
        }
        for(UUID targetuuid : this.targetPlayers){
            Player p = Bukkit.getPlayer(targetuuid);
            if(p == null) continue;
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10 * 20, 1));
        }
        owner.sendMessage("§a魔女たちの身体が光りだした！");
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
    }
}
