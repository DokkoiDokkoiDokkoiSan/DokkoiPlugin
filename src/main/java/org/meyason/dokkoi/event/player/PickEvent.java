package org.meyason.dokkoi.event.player;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.battleitem.ArcherArmor;
import org.meyason.dokkoi.item.jobitem.Ketsumou;
import org.meyason.dokkoi.job.Job;

public class PickEvent implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if(!item.hasItemMeta()){return;}

        Player player = event.getPlayer();
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
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
                    manager.addIsDeactivateDamageOnce(player, false);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack slotItem   = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        // Ketsumou判定
        java.util.function.Predicate<ItemStack> isKetsumou = stack -> {
            if (stack == null || !stack.hasItemMeta()) return false;
            CustomItem ci = CustomItem.getItem(stack);
            return ci instanceof Ketsumou;
        };

        boolean slotIsKetsumou   = isKetsumou.test(slotItem);
        boolean cursorIsKetsumou = isKetsumou.test(cursorItem);

        boolean clickedIsTop    = event.getClickedInventory() != null
                && event.getClickedInventory().equals(player.getOpenInventory().getTopInventory());
        boolean clickedIsBottom = event.getClickedInventory() != null
                && event.getClickedInventory().equals(player.getOpenInventory().getBottomInventory());


        Job job = Game.getInstance().getGameStatesManager().getPlayerJobs().get(player);

        // チェストのKetsumouをクリックし、シフトでインベントリに入れるとき
        if (clickedIsTop && slotIsKetsumou) {
            // シフトクリックで一気に移動
            if (event.getClick().isShiftClick()) {
                Ketsumou.activate(player);
            }
        }

        // インベントリのKetsumouをシフトでチェストに入れるとき
        if (clickedIsBottom && slotIsKetsumou) {
            if (event.getClick().isShiftClick()) {
                Ketsumou.deactivate(player);
            }
        }

        // インベントリのKetsumouとカーソルの別のアイテムを交代するとき
        // スロット: Ketsumou, カーソル: Ketsumou以外（減る）
        if (clickedIsBottom && slotIsKetsumou && !cursorIsKetsumou) {
            Ketsumou.deactivate(player);
        }

        //  カーソルのKetsumouとインベントリの別のアイテムを交代するとき
        // カーソル: Ketsumou, スロット: Ketsumou以外（増える）
        if (clickedIsBottom && cursorIsKetsumou && !slotIsKetsumou) {
            Ketsumou.activate(player);
        }

        // チェストプレートにArcherArmorを装備するとき
        if (clickedIsBottom && slotItem != null) {
            CustomItem customItem = CustomItem.getItem(slotItem);
            if(customItem instanceof ArcherArmor){
                Game.getInstance().getGameStatesManager().addIsDeactivateDamageOnce(player, true);
            }
        }
        // チェストプレートからArcherArmorを外すとき
        if (clickedIsBottom) {
            CustomItem customItem = CustomItem.getItem(cursorItem);
            if(customItem instanceof ArcherArmor){
                Game.getInstance().getGameStatesManager().addIsDeactivateDamageOnce(player, false);
            }
        }
    }

    @EventHandler
    public void onClosePlayerInventory(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        ItemStack chest = player.getInventory().getChestplate();
        boolean isArcherArmorEquipped = false;

        if (chest != null && chest.hasItemMeta()) {
            CustomItem ci = CustomItem.getItem(chest);
            if (ci instanceof ArcherArmor) {
                isArcherArmorEquipped = true;
            }
        }

        // ArcherArmor を装備しているなら true, 外れているなら false を設定
        Game.getInstance()
                .getGameStatesManager()
                .addIsDeactivateDamageOnce(player, isArcherArmorEquipped);

    }

    @EventHandler
    public void onPlayerPickItem(EntityPickupItemEvent event){
        if(!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        if(!item.hasItemMeta()){return;}
        Job job = Game.getInstance().getGameStatesManager().getPlayerJobs().get(player);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        if(container.has(itemKey)){
            String itemName = container.get(itemKey, org.bukkit.persistence.PersistentDataType.STRING);
            if(itemName != null){
                CustomItem customItem = CustomItem.getItem(item);
                if(customItem instanceof Ketsumou){
                    Ketsumou.activate(player);
                }
            }
        }
    }
}
