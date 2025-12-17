package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
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
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.item.jobitem.*;
import org.meyason.dokkoi.item.jobitem.gacha.*;
import org.meyason.dokkoi.item.utilitem.*;
import org.meyason.dokkoi.item.weapon.DragonBrade;
import org.meyason.dokkoi.item.weapon.DrainBrade;
import org.meyason.dokkoi.job.*;

import java.util.Objects;
import java.util.UUID;

public class PickEvent implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        int amount = item.getAmount();
        if(!item.hasItemMeta()){return;}

        Player player = event.getPlayer();
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Job job = manager.getPlayerJobs().get(player.getUniqueId());
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);

        if(container.has(itemKey)){
            String itemName = container.get(itemKey, org.bukkit.persistence.PersistentDataType.STRING);
            if(isUniqueItem(item)){
                event.setCancelled(true);
            }
            if(itemName != null){

                switch (itemName) {
                    case Ketsumou.id -> Ketsumou.deactivate(player);

                    case ArcherArmor.id -> manager.addIsDeactivateDamageOnce(player.getUniqueId(), false);

                    case StrongestStrongestBall.id -> {
                        event.setCancelled(true);
                        player.sendActionBar(Component.text("§aもっと最強のたまたま§bが手から離れない！？"));
                    }

                    case Korehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            player.sendMessage(Component.text("§cこれはすてたくない"));
                            event.setCancelled(true);
                        }
                    }

                    case TakashimaPhone.id -> manager.clearWhoHasTakashimaPhone();

                    case MamiyaPhone.id -> manager.clearWhoHasMamiyaPhone();

                    case InstantDevour.id -> InstantDevour.changeHP(player, -amount);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        if(manager.getGameState() != GameState.IN_GAME){
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack slotItem   = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        boolean clickedIsTop    = event.getClickedInventory() != null
                && event.getClickedInventory().equals(player.getOpenInventory().getTopInventory());
        boolean clickedIsBottom = event.getClickedInventory() != null
                && event.getClickedInventory().equals(player.getOpenInventory().getBottomInventory());

        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);

        Job job = manager.getPlayerJobs().get(player.getUniqueId());

        // シフトクリックのとき
        if(event.getClick().isShiftClick()){
            if(slotItem == null){return;}
            ItemMeta slotMeta = slotItem.getItemMeta();
            if(slotMeta == null){return;}
            PersistentDataContainer container = slotMeta.getPersistentDataContainer();
            if(!container.has(itemKey, PersistentDataType.STRING)){return;}
            String slotItemName = container.get(itemKey, PersistentDataType.STRING);
            if(slotItemName == null){return;}

            // チェスト内のアイテムをシフトクリックしてインベントリに移した時
            if (clickedIsTop) {
                int amount = slotItem.getAmount();

                switch (slotItemName) {
                    case Ketsumou.id -> Ketsumou.activate(player);

                    case Korehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            for (UUID uuid : manager.getAlivePlayers()) {
                                Job targetJob = manager.getPlayerJobs().get(uuid);
                                if (targetJob instanceof DrugStore drugStore) {
                                    drugStore.incrementPickCount();
                                    Korehamaru.activate(player);
                                    break;
                                }
                            }
                        }
                    }

                    case TotemoKorehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            Player killer = manager.getPlayerJobs().entrySet().stream()
                                    .filter(entry -> entry.getValue() instanceof DrugStore)
                                    .map(entry -> manager.getAlivePlayers().contains(entry.getKey()) ? entry.getKey() : null)
                                    .filter(Objects::nonNull)
                                    .map(uuid -> player.getServer().getPlayer(uuid))
                                    .findFirst()
                                    .orElse(null);
                            cursorItem.setAmount(0);
                            player.sendMessage(Component.text("§cOD発動！"));
                            DeathEvent.kill(killer, player);
                        }
                    }

                    case TakashimaPhone.id -> manager.updatePlayerhasTakashimaPhone(player.getUniqueId());

                    case MamiyaPhone.id -> manager.updatePlayerhasMamiyaPhone(player.getUniqueId());

                    case InstantDevour.id -> InstantDevour.changeHP(player, amount);
                }
                return;

            // インベントリ内のアイテムをシフトクリックしてチェストに移した時
            }else if(clickedIsBottom){
                int amount = slotItem.getAmount();
                if(isUniqueItem(slotItem)){
                    event.setCancelled(true);
                }

                switch (slotItemName) {
                    case Ketsumou.id -> Ketsumou.deactivate(player);

                    case Korehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            player.sendMessage(Component.text("§cこれはすてたくない"));
                            event.setCancelled(true);
                        }
                    }

                    case TakashimaPhone.id -> manager.clearWhoHasTakashimaPhone();

                    case MamiyaPhone.id -> manager.clearWhoHasMamiyaPhone();

                    case InstantDevour.id -> InstantDevour.changeHP(player, -amount);
                }
                return;
            }
        }else if(event.getClick() == ClickType.CONTROL_DROP){
            if(clickedIsTop){
                return;
            }
            // コントロールドロップのとき(カーソルを合わせてCtrl+Q)
            if(slotItem == null){return;}
            ItemMeta slotMeta = slotItem.getItemMeta();
            if(slotMeta == null){return;}
            PersistentDataContainer container = slotMeta.getPersistentDataContainer();
            if(!container.has(itemKey, PersistentDataType.STRING)){return;}
            String slotItemName = container.get(itemKey, PersistentDataType.STRING);
            if(slotItemName == null){return;}

            int amount = slotItem.getAmount();

            switch (slotItemName) {
                case Ketsumou.id -> Ketsumou.deactivate(player);

                case ArcherArmor.id -> manager.addIsDeactivateDamageOnce(player.getUniqueId(), false);

                case StrongestStrongestBall.id -> {
                    event.setCancelled(true);
                    player.sendActionBar(Component.text("§aもっと最強のたまたま§bが手から離れない！？"));
                }

                case Skill.id, Ultimate.id, Passive.id -> event.setCancelled(true);

                case Korehamaru.id -> {
                    if (!(job instanceof DrugStore)) {
                        player.sendMessage(Component.text("§cこれはすてたくない"));
                        event.setCancelled(true);
                    }
                }

                case TakashimaPhone.id -> manager.clearWhoHasTakashimaPhone();

                case MamiyaPhone.id -> manager.clearWhoHasMamiyaPhone();

                case InstantDevour.id -> InstantDevour.changeHP(player, -amount);
            }
            return;
        }

        // 通常クリックのとき
        // インベントリをクリックしたとき
        if(clickedIsBottom) {

            // カーソルで指定アイテムを持っているとき(クリックでおこうとしたとき)
            if(cursorItem != null && !cursorItem.getType().isAir()) {
                int amount = cursorItem.getAmount();
                if(event.getClick() == ClickType.RIGHT){
                    amount = 1;
                }
                ItemMeta meta = cursorItem.getItemMeta();
                if(meta == null){return;}
                PersistentDataContainer container = meta.getPersistentDataContainer();
                String cursorItemName = container.get(itemKey, PersistentDataType.STRING);
                if(cursorItemName == null){return;}

                switch (cursorItemName) {
                    case Korehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            for (UUID uuid : Game.getInstance().getGameStatesManager().getAlivePlayers()) {
                                Job targetJob = Game.getInstance().getGameStatesManager().getPlayerJobs().get(uuid);
                                if (targetJob instanceof DrugStore drugStore) {
                                    drugStore.incrementPickCount();
                                    Korehamaru.activate(player);
                                    break;
                                }
                            }
                        }
                    }

                    case Ketsumou.id -> Ketsumou.activate(player);

                    case TotemoKorehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            Player killer = Game.getInstance().getGameStatesManager().getPlayerJobs().entrySet().stream()
                                    .filter(entry -> entry.getValue() instanceof DrugStore)
                                    .map(entry -> Game.getInstance().getGameStatesManager().getAlivePlayers().contains(entry.getKey()) ? entry.getKey() : null)
                                    .filter(Objects::nonNull)
                                    .map(uuid -> player.getServer().getPlayer(uuid))
                                    .findFirst()
                                    .orElse(null);
                            cursorItem.setAmount(0);
                            player.sendMessage(Component.text("§cOD発動！"));
                            DeathEvent.kill(killer, player);
                        }
                    }

                    case TakashimaPhone.id -> manager.updatePlayerhasTakashimaPhone(player.getUniqueId());

                    case MamiyaPhone.id -> manager.updatePlayerhasMamiyaPhone(player.getUniqueId());

                    case InstantDevour.id -> InstantDevour.changeHP(player, amount);
                }
                return;
            }

            // スロットが指定アイテムのとき(クリックで持ち上げたとき)
            if(slotItem != null && !slotItem.getType().isAir()){
                int amount = slotItem.getAmount();
                if(event.getClick() == ClickType.RIGHT){
                    if(amount % 2 == 0){
                        amount = amount / 2;
                    }else{
                        amount = (amount + 1) / 2;
                    }
                }
                ItemMeta meta = slotItem.getItemMeta();
                if(meta == null){return;}
                PersistentDataContainer container = meta.getPersistentDataContainer();
                String slotItemName = container.get(itemKey, PersistentDataType.STRING);
                if(slotItemName == null){return;}

                if(isUniqueItem(slotItem)){
                    event.setCancelled(true);
                }

                switch (slotItemName) {

                    case Korehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            player.sendMessage(Component.text("§cこれはすてたくない"));
                            event.setCancelled(true);
                        }
                    }

                    case Ketsumou.id -> Ketsumou.deactivate(player);

                    case TakashimaPhone.id -> manager.clearWhoHasTakashimaPhone();

                    case MamiyaPhone.id -> manager.clearWhoHasMamiyaPhone();

                    case InstantDevour.id -> InstantDevour.changeHP(player, -amount);
                }
                return;
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
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        if(manager.getGameState() != GameState.IN_GAME){
            return;
        }

        if(manager.isNaito(player.getUniqueId())){
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        if(!item.hasItemMeta()){return;}
        Job job = manager.getPlayerJobs().get(player.getUniqueId());
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        if(container.has(itemKey)){
            String itemName = container.get(itemKey, org.bukkit.persistence.PersistentDataType.STRING);
            if(itemName != null){

                switch (itemName) {
                    case Ketsumou.id -> Ketsumou.activate(player);

                    case Korehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            for (UUID uuid : manager.getAlivePlayers()) {
                                Job targetJob = manager.getPlayerJobs().get(uuid);
                                if (targetJob instanceof DrugStore drugStore) {
                                    drugStore.incrementPickCount();
                                    Korehamaru.activate(player);
                                    break;
                                }
                            }
                        }
                    }

                    case TotemoKorehamaru.id -> {
                        if (!(job instanceof DrugStore)) {
                            Player killer = manager.getPlayerJobs().entrySet().stream()
                                    .filter(entry -> entry.getValue() instanceof DrugStore)
                                    .map(entry -> manager.getAlivePlayers().contains(entry.getKey()) ? entry.getKey() : null)
                                    .filter(Objects::nonNull)
                                    .map(uuid -> player.getServer().getPlayer(uuid))
                                    .filter(Objects::nonNull)
                                    .findFirst()
                                    .orElse(null);
                            event.setCancelled(true);
                            event.getItem().remove();
                            player.sendActionBar(Component.text("§cOD発動！"));
                            DeathEvent.kill(killer, player);
                        }
                    }

                    case MamiyaPhone.id -> manager.updatePlayerhasMamiyaPhone(player.getUniqueId());

                    case TakashimaPhone.id -> manager.updatePlayerhasTakashimaPhone(player.getUniqueId());

                    case InstantDevour.id -> InstantDevour.changeHP(player, item.getAmount());
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
                    if(itemName.equals(StrongestStrongestBall.id)){
                        player.sendActionBar(Component.text("§aもっと最強のたまたま§bのさわやかな風に乗った。"));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 10, false, false, true));
                    }else if(itemName.equals(DragonBrade.id)){
                        player.sendActionBar(Component.text("§a龍一文字§eの力がみなぎってきた！"));
                        DragonBrade.activate(player);
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
                    if(itemName.equals(StrongestStrongestBall.id)){
                        player.removePotionEffect(PotionEffectType.SPEED);
                    }else if(itemName.equals(DragonBrade.id)){
                        DragonBrade.deactivate(player);
                    }
                }
            }
        }
    }

    private boolean isUniqueItem(ItemStack item){
        CustomItem customItem;
        try{
            customItem = CustomItem.getItem(item);
        } catch(NoGameItemException e){
            return false;
        }
        return Objects.requireNonNull(customItem).isUnique;
    }
}
