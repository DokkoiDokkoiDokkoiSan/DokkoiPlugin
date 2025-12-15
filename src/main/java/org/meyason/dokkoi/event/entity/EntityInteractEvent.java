package org.meyason.dokkoi.event.entity;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.entity.Clerk;
import org.meyason.dokkoi.entity.Dealer;
import org.meyason.dokkoi.entity.GameEntity;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.battleitem.PotionBottleEmpty;
import org.meyason.dokkoi.menu.shopmenu.ShopMenu;

public class EntityInteractEvent implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(entity instanceof Villager villager){
            event.setCancelled(true);
            if(villager.getPersistentDataContainer().isEmpty()) {
                return;
            }
            NamespacedKey npcKey = new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.NPC);
            NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);

            String npcID = villager.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
            if(npcID == null){return;}


            GameEntity gameEntity = manager.getSpawnedEntitiesFromUUID(npcID);

            if(gameEntity instanceof Dealer dealer){
                if(!item.hasItemMeta()){
                    dealer.talk(player);
                    return;
                }
                boolean result = false;
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if(container.has(itemKey, PersistentDataType.STRING)){
                    CustomItem customItem = CustomItem.getItem(item);
                    if(customItem != null){
                        result = dealer.giveDrag(player, customItem);
                    }
                }
                if(result){
                    manager.removeSpawnedEntity(npcID);
                    villager.remove();
                }

            }else if(gameEntity instanceof Clerk clerk){
                ItemMeta meta = item.getItemMeta();
                if(meta != null){
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    if(container.has(itemKey, PersistentDataType.STRING)){
                        String itemName = container.get(itemKey, PersistentDataType.STRING);
                        if(itemName != null && itemName.equals(PotionBottleEmpty.id)){
                            PotionBottleEmpty.activate(player, item);
                            return;
                        }
                    }
                }
                clerk.talk(player);
                ShopMenu shopMenu = new ShopMenu();
                shopMenu.sendMenu(clerk, player);
            }

        }
    }
}
