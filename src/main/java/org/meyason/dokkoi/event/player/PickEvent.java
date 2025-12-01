package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.battleitem.ArcherArmor;
import org.meyason.dokkoi.item.dealeritem.Korehamaru;
import org.meyason.dokkoi.item.dealeritem.TotemoKorehamaru;
import org.meyason.dokkoi.item.jobitem.Ketsumou;
import org.meyason.dokkoi.item.jobitem.Passive;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.item.jobitem.Ultimate;
import org.meyason.dokkoi.item.jobitem.gacha.StrongestStrongestBall;
import org.meyason.dokkoi.job.DrugStore;
import org.meyason.dokkoi.job.Job;

import java.util.Objects;
import java.util.UUID;

public class PickEvent implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if(!item.hasItemMeta()){return;}

        Player player = event.getPlayer();
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Job job = manager.getPlayerJobs().get(player.getUniqueId());
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);

        if(container.has(itemKey)){
            String itemName = container.get(itemKey, org.bukkit.persistence.PersistentDataType.STRING);
            if(itemName != null){

                CustomItem customItem = CustomItem.getItem(item);
                if(customItem instanceof Ketsumou){
                    Ketsumou.deactivate(player);
                }else if(customItem instanceof ArcherArmor){
                    manager.addIsDeactivateDamageOnce(player.getUniqueId(), false);
                }else if(customItem instanceof StrongestStrongestBall){
                    event.setCancelled(true);
                    player.sendActionBar(Component.text("§aもっと最強のたまたま§bが手から離れない！？"));

                }else if(customItem instanceof Skill){
                    event.setCancelled(true);
                }else if(customItem instanceof Ultimate){
                    event.setCancelled(true);
                }else if(customItem instanceof Passive){
                    event.setCancelled(true);
                }else if(customItem instanceof Korehamaru){
                    if(!(job instanceof DrugStore)){
                        player.sendActionBar(Component.text("§cこれはすてたくない"));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(Game.getInstance().getGameStatesManager().getGameState() != GameState.IN_GAME){
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack slotItem   = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        boolean clickedIsTop    = event.getClickedInventory() != null
                && event.getClickedInventory().equals(player.getOpenInventory().getTopInventory());
        boolean clickedIsBottom = event.getClickedInventory() != null
                && event.getClickedInventory().equals(player.getOpenInventory().getBottomInventory());

        Job job = Game.getInstance().getGameStatesManager().getPlayerJobs().get(player.getUniqueId());

        // シフトクリックのとき
        if(event.getClick().isShiftClick()){
            if(slotItem == null){return;}
            if(!slotItem.hasItemMeta()){return;}
            CustomItem slotCustomItem = CustomItem.getItem(slotItem);

            // チェスト内のアイテムをシフトクリックしてインベントリに移した時
            if (clickedIsTop) {
                if(slotCustomItem instanceof Ketsumou) {
                    Ketsumou.activate(player);
                }else if(slotCustomItem instanceof Korehamaru){
                    if(!(job instanceof DrugStore)){
                        for(UUID uuid : Game.getInstance().getGameStatesManager().getAlivePlayers()){
                            Job targetJob = Game.getInstance().getGameStatesManager().getPlayerJobs().get(uuid);
                            if(targetJob instanceof DrugStore drugStore){
                                drugStore.incrementPickCount();
                                break;
                            }
                        }
                        player.sendMessage(Component.text("§cあたまがふらふらしてきた..."));
                    }
                }
                return;

            // インベントリ内のアイテムをシフトクリックしてチェストに移した時
            }else if(clickedIsBottom){
                if(slotCustomItem instanceof Ketsumou) {
                    Ketsumou.deactivate(player);
                }else if(slotCustomItem instanceof Skill || slotCustomItem instanceof Ultimate || slotCustomItem instanceof Passive){
                    event.setCancelled(true);
                }else if(slotCustomItem instanceof Korehamaru){
                    if(!(job instanceof DrugStore)){
                        player.sendMessage(Component.text("§cこれはすてたくない"));
                        event.setCancelled(true);
                    }
                }
                return;
            }
        }

        // 通常クリックのとき
        // インベントリをクリックしたとき
        if(clickedIsBottom) {

            // スロットが指定アイテムのとき(クリックで持ち上げたとき)
            if(slotItem != null && !slotItem.getType().isAir()){
                ItemMeta meta = slotItem.getItemMeta();
                if(meta == null){return;}
                CustomItem slotCustomItem = CustomItem.getItem(slotItem);
                if(slotCustomItem instanceof Skill || slotCustomItem instanceof Ultimate || slotCustomItem instanceof Passive){
                    event.setCancelled(true);
                }else if(slotCustomItem instanceof Korehamaru){
                    if(!(job instanceof DrugStore)){
                        player.sendMessage(Component.text("§cこれはすてたくない"));
                        event.setCancelled(true);
                    }
                }else if(slotCustomItem instanceof Ketsumou){
                    Ketsumou.deactivate(player);
                }
            }

            // カーソルで指定アイテムを持っているとき(クリックでおこうとしたとき)
            if(cursorItem != null && !cursorItem.getType().isAir()) {
                ItemMeta meta = cursorItem.getItemMeta();
                if (meta != null) {
                    CustomItem cursorCustomItem = CustomItem.getItem(cursorItem);
                    if (cursorCustomItem instanceof Korehamaru) {
                        if (!(job instanceof DrugStore)) {
                            for (UUID uuid : Game.getInstance().getGameStatesManager().getAlivePlayers()) {
                                Job targetJob = Game.getInstance().getGameStatesManager().getPlayerJobs().get(uuid);
                                if (targetJob instanceof DrugStore drugStore) {
                                    drugStore.incrementPickCount();
                                    break;
                                }
                            }
                            player.sendMessage(Component.text("§cあたまがふらふらしてきた..."));
                        }
                    } else if (cursorCustomItem instanceof Ketsumou) {
                        Ketsumou.activate(player);
                    } else if(cursorCustomItem instanceof TotemoKorehamaru){
                        if(!(job instanceof DrugStore)){
                            Player killer = Game.getInstance().getGameStatesManager().getPlayerJobs().entrySet().stream()
                                    .filter(entry -> entry.getValue() instanceof DrugStore)
                                    .map(entry -> Game.getInstance().getGameStatesManager().getAlivePlayers().contains(entry.getKey()) ? entry.getKey() : null)
                                    .filter(Objects::nonNull)
                                    .map(uuid -> player.getServer().getPlayer(uuid))
                                    .findFirst()
                                    .orElse(null);
                            if(killer != null) {
                                cursorItem.setAmount(0);
                                player.sendActionBar(Component.text("§cOD発動！"));
                                DeathEvent.kill(killer, player);
                            }
                        }
                    }
                }
            }

        }else{
            // チェストをクリックしたとき

            // カーソルに指定アイテムがあって置いたとき
//            if(cursorItem != null && !cursorItem.getType().isAir()) {
//                ItemMeta meta = cursorItem.getItemMeta();
//                if (meta != null) {
//                    CustomItem cursorCustomItem = CustomItem.getItem(cursorItem);
//                    if (cursorCustomItem instanceof Ketsumou) {
//                        Ketsumou.deactivate(player);
//                    }
//                }
//            }

            // スロットが指定アイテムのとき
//            if(slotItem != null){
//                ItemMeta meta = slotItem.getItemMeta();
//                if(meta == null){return;}
//
//            }
        }
    }

    @EventHandler
    public void onClosePlayerInventory(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        PlayerInventory playerInventory = player.getInventory();

        ItemStack chest = playerInventory.getChestplate();
        boolean isArcherArmorEquipped = false;

        if (chest != null && chest.hasItemMeta()) {
            ItemMeta meta = chest.getItemMeta();
            if(meta != null){
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
                if(container.has(itemKey, PersistentDataType.STRING)){
                    if(Objects.equals(container.get(itemKey, PersistentDataType.STRING), ArcherArmor.id)){
                        isArcherArmorEquipped = true;

                    }
                }
            }
        }

        Game.getInstance().getGameStatesManager().addIsDeactivateDamageOnce(player.getUniqueId(), isArcherArmorEquipped);

    }

    @EventHandler
    public void onPlayerPickItem(EntityPickupItemEvent event){
        if(!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        if(!item.hasItemMeta()){return;}
        Job job = Game.getInstance().getGameStatesManager().getPlayerJobs().get(player.getUniqueId());
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        if(container.has(itemKey)){
            String itemName = container.get(itemKey, org.bukkit.persistence.PersistentDataType.STRING);
            if(itemName != null){
                CustomItem customItem = CustomItem.getItem(item);
                if(customItem instanceof Ketsumou){
                    Ketsumou.activate(player);
                }else if(customItem instanceof Korehamaru){
                    if(!(job instanceof DrugStore)){
                        player.sendActionBar(Component.text("§cあたまがふらふらしてきた..."));
                        for(UUID uuid : Game.getInstance().getGameStatesManager().getAlivePlayers()){
                            Job targetJob = Game.getInstance().getGameStatesManager().getPlayerJobs().get(uuid);
                            if(targetJob instanceof DrugStore drugStore){
                                drugStore.incrementPickCount();
                                break;
                            }
                        }
                    }
                }else if(customItem instanceof TotemoKorehamaru){
                    if(!(job instanceof DrugStore)){
                        Player killer = Game.getInstance().getGameStatesManager().getPlayerJobs().entrySet().stream()
                                .filter(entry -> entry.getValue() instanceof DrugStore)
                                .map(entry -> Game.getInstance().getGameStatesManager().getAlivePlayers().contains(entry.getKey()) ? entry.getKey() : null)
                                .filter(Objects::nonNull)
                                .map(uuid -> player.getServer().getPlayer(uuid))
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(null);
                        if(killer != null) {
                            item.setAmount(0);
                            player.sendActionBar(Component.text("§cOD発動！"));
                            DeathEvent.kill(killer, player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if(newItem != null && newItem.hasItemMeta()){
            ItemMeta meta = newItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
            if(container.has(itemKey)){
                String itemName = container.get(itemKey, org.bukkit.persistence.PersistentDataType.STRING);
                if(itemName != null){
                    CustomItem customItem = CustomItem.getItem(newItem);
                    if(customItem instanceof StrongestStrongestBall){
                        player.sendActionBar(Component.text("§aもっと最強のたまたま§bのさわやかな風に乗った。"));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 10, false, false, true));
                    }
                }
            }
        }

        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        if(oldItem != null && oldItem.hasItemMeta()){
            ItemMeta meta = oldItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
            if(container.has(itemKey)){
                String itemName = container.get(itemKey, org.bukkit.persistence.PersistentDataType.STRING);
                if(itemName != null){
                    CustomItem customItem = CustomItem.getItem(oldItem);
                    if(customItem instanceof StrongestStrongestBall){
                        player.removePotionEffect(PotionEffectType.SPEED);
                    }
                }
            }
        }
    }
}
