package org.meyason.dokkoi.item.gacha.menu.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.meyason.dokkoi.goal.GachaAddict;
import org.meyason.dokkoi.item.gacha.GachaMachine;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;

public class GachaPointItem extends AbstractItem {

    private final Material material;

    public GachaPointItem(Material material) {this.material = material;}

    @Override
    public ItemProvider getItemProvider(){
        int point = GachaAddict.pointMap.get(material);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            meta.setDisplayName("§a§l" + GachaAddict.nameMap.get(material) + " §f§l取得ポイント: §e§l " + point);
            List<Component> lore = List.of(
                    Component.text("§7クリックでポイントに変換します。")
            );
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return new ItemBuilder(item);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        GachaMachine.exchangeGachaPoint(player, this.material);
    }
}
