package org.meyason.dokkoi.item.goalitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
import java.util.UUID;

public class KillerList extends CustomItem {

    public static final String id = "killer_list";

    private Player player;
    private Game game;

    private List<UUID> targetPlayerList = new ArrayList<>();

    public KillerList() {
        super(id, "§a殺すノート", ItemStack.of(Material.WRITTEN_BOOK), 1);
        isUnique = true;
        hasSerialNumber = true;
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
            this.baseItem = item;
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
        if(this.player == null || this.game == null){
            return;
        }
        PlayerInventory inventory = this.player.getInventory();
        int slot = findKillerListSlot(inventory);
        if(slot == -1){
            return;
        }
        ItemStack killerListItem = inventory.getItem(slot);
        if(killerListItem == null){
            return;
        }
        ItemMeta meta = killerListItem.getItemMeta();
        if(!(meta instanceof BookMeta bookMeta)){
            return;
        }
        List<UUID> killerPlayers = new ArrayList<>(game.getGameStatesManager().getKillerList().keySet());
        StringBuilder names = new StringBuilder();
        List<UUID> updatedTargets = new ArrayList<>();
        for(UUID id : killerPlayers){
            if(id.equals(player.getUniqueId())) continue;
            Player p = Bukkit.getPlayer(id);
            if(p == null) continue;
            boolean isAlive = game.getGameStatesManager().getAlivePlayers().contains(id);
            if(!isAlive){
                names.append("§4§m- ").append(p.getName()).append("\n");
                continue;
            }
            names.append("§2- ").append(p.getName()).append("\n");
            updatedTargets.add(id);
        }
        bookMeta.setPages(names.toString());
        killerListItem.setItemMeta(bookMeta);
        inventory.setItem(slot, killerListItem);
        this.baseItem = killerListItem.clone();
        this.targetPlayerList = updatedTargets;
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
    }

    public List<UUID> getTargetPlayerList() {
        return targetPlayerList;
    }

    private int findKillerListSlot(PlayerInventory inventory){
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        for(int slot = 0; slot < inventory.getSize(); slot++){
            ItemStack stack = inventory.getItem(slot);
            if(stack == null) continue;
            ItemMeta meta = stack.getItemMeta();
            if(meta == null) continue;
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if(container.has(itemKey, PersistentDataType.STRING) &&
               KillerList.id.equals(container.get(itemKey, PersistentDataType.STRING))){
                return slot;
            }
        }
        return -1;
    }
}
