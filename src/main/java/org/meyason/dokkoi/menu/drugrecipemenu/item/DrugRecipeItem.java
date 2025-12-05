package org.meyason.dokkoi.menu.drugrecipemenu.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.job.DrugStore;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class DrugRecipeItem extends AbstractItem {

    private String itemID;

    private String need1;
    private String need2;
    private String need3;

    public DrugRecipeItem(String itemID, String need1, String need2, String need3) {
        this.itemID = itemID;
        this.need1 = need1;
        this.need2 = need2;
        this.need3 = need3;
    }

    @Override
    public ItemProvider getItemProvider() {
        CustomItem customItem = null;
        try {
            customItem = GameItem.getItem(itemID);
        } catch (NoGameItemException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ItemStack itemStack = new ItemStack(Material.MELON_SEEDS);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (customItem != null && itemMeta != null) {
            itemMeta.setDisplayName(customItem.getName());
            List<Component> lore = customItem.getDescription();
            itemMeta.lore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        return new ItemBuilder(itemStack);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        PlayerInventory inventory = player.getInventory();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        boolean hasNeed1 = false;
        boolean hasNeed2 = false;
        boolean hasNeed3 = false;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) continue;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) continue;
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(itemKey)) {
                String isItem = container.get(itemKey, PersistentDataType.STRING);
                if (isItem != null) {
                    if (isItem.equals(need1)) {
                        hasNeed1 = true;
                    }
                    if (isItem.equals(need2)) {
                        hasNeed2 = true;
                    }
                    if (isItem.equals(need3)) {
                        hasNeed3 = true;
                    }
                }
            }
        }
        if (hasNeed1 && hasNeed2 && hasNeed3) {
            // Remove required items
            GameItem.removeItem(player, need1, 1);
            GameItem.removeItem(player, need2, 1);
            GameItem.removeItem(player, need3, 1);

            // Give the crafted item
            CustomItem craftedItem = GameItem.getItem(itemID);
            if (craftedItem != null) {
                ItemStack craftedItemStack = craftedItem.getItem();
                inventory.addItem(craftedItemStack);
                player.sendMessage(craftedItem.getName() + "§aの調合に成功しました");
                DrugStore drugStore = (DrugStore) Game.getInstance().getGameStatesManager().getPlayerJobs().get(player.getUniqueId());
                drugStore.setCoolTimeSkill(5);
                drugStore.setRemainCoolTimeSkill(5);
                drugStore.chargeSkill(player, Game.getInstance().getGameStatesManager());
                player.closeInventory();
            }
        }else{
            player.sendMessage("§c材料が足りません");
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
}
