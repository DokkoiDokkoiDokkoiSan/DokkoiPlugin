package org.meyason.dokkoi.item;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;

import java.util.function.Function;

public abstract class CustomItem {

    protected final String id;
    protected final String name;
    protected final ItemStack baseItem;

    public boolean isUnique = false;

    protected Function<ItemStack, ItemStack> default_setting;

    public CustomItem(String id, String name, ItemStack baseItem){
        this.id = id;
        this.name = name;
        this.baseItem = baseItem;
    }

    public String getId() {return this.id;}

    public String getName() {return this.name;}

    public ItemStack getBaseItem() {return this.baseItem;}

    public ItemStack getItem(){
        ItemStack item = getBaseItem().clone();
        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME), PersistentDataType.STRING, this.name);
            meta.displayName(Component.text(this.name));
            item.setItemMeta(meta);
            return default_setting.apply(item);
        }
        return item;
    }

    protected abstract void registerItemFunction();
}
