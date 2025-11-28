package org.meyason.dokkoi.item;

import com.google.gson.InstanceCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.dealeritem.Tsuyokunaru;
import org.meyason.dokkoi.item.food.*;
import org.meyason.dokkoi.item.jobitem.gacha.*;
import org.meyason.dokkoi.item.goalitem.*;
import org.meyason.dokkoi.item.jobitem.*;
import org.meyason.dokkoi.item.weapon.*;
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.item.battleitems.HealingCrystal;
import org.meyason.dokkoi.item.jobitem.gacha.*;
import org.meyason.dokkoi.item.goalitem.*;
import org.meyason.dokkoi.item.jobitem.*;

import java.util.HashMap;

public class GameItem {

    private static HashMap<String, CustomItem> items = new HashMap<>();

    public GameItem(){
        registerItem();
    }

    public void registerItem(){
        items.put(GameItemKeyString.SKILL, new Skill());
        items.put(GameItemKeyString.ULTIMATE_SKILL, new Ultimate());
        items.put(GameItemKeyString.PASSIVE_SKILL, new Passive());
        items.put(GameItemKeyString.KILLER_LIST, new KillerList());
        items.put(GameItemKeyString.RAPIER, new Rapier());
        items.put(GameItemKeyString.TIERPLAYERLIST, new TierPlayerList());
        items.put(GameItemKeyString.KETSUMOU, new Ketsumou());
        items.put(GameItemKeyString.HEALINGCRYSTAL, new HealingCrystal());
        items.put(GameItemKeyString.BURIBURIGUARD, new BuriBuriGuard());
        items.put(GameItemKeyString.STRONGESTBALL, new StrongestBall());
        items.put(GameItemKeyString.STRONGESTSTRONGESTBALL, new StrongestStrongestBall());
        items.put(GameItemKeyString.STRONGESTSTRONGESTSTRONGESTBALL, new StrongestStrongestStrongestBall());
        items.put(GameItemKeyString.GOLDENCARROT, new GoldenCarrot());
        items.put(GameItemKeyString.LONGSWORD, new LongSword());
        items.put(GameItemKeyString.ARCHERARMOR, new ArcherArmor());
        items.put(GameItemKeyString.BAKEDPOTATO, new BakedPotato());
        items.put(GameItemKeyString.THUNDERJAVELIN, new ThunderJavelin());
        items.put(GameItemKeyString.REDHELMET, new RedHelmet());
    }

    public static CustomItem getItem(String id){
        if(!items.containsKey(id)){
            throw new NoGameItemException("untilized item id: " + id);
        }
        return items.get(id).clone();
    }

    public static Boolean removeItem(Player player, String item_name, int amount){
        PlayerInventory inventory = player.getInventory();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);

        for(ItemStack item : inventory.getContents()){
            if(item != null && item.getItemMeta() != null){
                if(item.getItemMeta().getPersistentDataContainer().has(itemKey) &&
                   item.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING).equals(item_name)){

                    int itemAmount = item.getAmount();
                    if(itemAmount >= amount){
                        item.setAmount(itemAmount - amount);
                        return true;
                    } else {
                        amount -= itemAmount;
                        item.setAmount(0);
                    }
                }
            }
        }
        return false;
    }

    public static String[] getItemIds(){
        return items.keySet().toArray(new String[0]);
    }

    public static boolean isCustomItem(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        if(meta != null){
            PersistentDataContainer container = meta.getPersistentDataContainer();
            return container.has(itemKey, PersistentDataType.STRING);
        }
        return false;
    }
}
