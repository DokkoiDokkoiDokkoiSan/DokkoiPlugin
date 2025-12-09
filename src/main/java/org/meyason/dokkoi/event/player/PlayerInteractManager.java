package org.meyason.dokkoi.event.player;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.gunitem.GunItem;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.item.jobitem.Ultimate;

public class PlayerInteractManager implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        String itemID = container.get(itemKey, PersistentDataType.STRING);
        if (itemID == null) {
            return;
        }
        CustomItem customItem;
        try {
            customItem = GameItem.getItem(itemID);
        } catch (NoGameItemException e) {
            return;
        }

        NamespacedKey uniqueIdKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.UNIQUE_ITEM);
        NamespacedKey gunSerialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.GUN_SERIAL);


        String gunSerial = container.get(gunSerialKey,  PersistentDataType.STRING);
        if(gunSerial != null) {
            GunShootEvent.onGunShoot(event, gunSerial, (GunItem) customItem);
            return;
        }

        if(itemID.equals(Skill.id) || itemID.equals(Ultimate.id)){
            SkillInteractEvent.onSkillInteract(event, itemID);
            return;
        }

        String itemSerial = container.get(uniqueIdKey,  PersistentDataType.STRING);
        ItemInteractEvent.onItemInteract(event, itemID, itemSerial, customItem, item);
    }
}
