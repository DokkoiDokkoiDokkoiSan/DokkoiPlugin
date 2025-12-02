package org.meyason.dokkoi.menu.shopmenu.item;

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
        this.itemStack = item.getBaseItem();
        this.price = clerk.getItemPrice(id);
    }


    @Override
    public ItemProvider getItemProvider() {
        int amount = 1;
        if(itemStack.getType() == Material.ARROW){
            amount = 32;
        }
        return new ItemBuilder(itemStack.getType()).setAmount(amount).setDisplayName(this.customItem.getName()).addLoreLines(
                    this.customItem.getDescription().toString(),
                "",
                "§6価格: §a" + this.price + "モネイ"

        );
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if(clerk.canBuyItem(player, id)){
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        }else{
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
    }
}
