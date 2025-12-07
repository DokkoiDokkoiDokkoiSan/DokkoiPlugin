package org.meyason.dokkoi.menu.shopmenu.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.entity.Clerk;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.weapon.Arrow;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class ShopMenuItem extends AbstractItem {

    private CustomItem customItem;

    private ItemStack itemStack;

    private int price;

    private Clerk clerk;

    private String id;

    public ShopMenuItem(String id, Clerk clerk) {
        this.id = id;
        this.clerk = clerk;
        CustomItem item;
        try {
            item = GameItem.getItem(id);
        } catch (NoGameItemException e) {
            e.printStackTrace();
            return;
        }
        this.customItem = item;
        this.itemStack = item.getItem();
        this.price = clerk.getItemPrice(id);
    }


    @Override
    public ItemProvider getItemProvider() {
        int amount = 1;
        if (customItem instanceof Arrow) {
            itemStack.setAmount(32);
        }
        List<Component> description = this.customItem.getDescription();
        ItemBuilder builder =  new ItemBuilder(itemStack.getType()).setAmount(amount).setDisplayName(this.customItem.getName());
        for(Component line : description){
            builder.addLoreLines( new AdventureComponentWrapper(line));
        }
        builder.addLoreLines( new AdventureComponentWrapper(Component.text("§e価格: " + price + "モネイ")));
        return builder;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clerk.canBuyItem(player, id)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
}
