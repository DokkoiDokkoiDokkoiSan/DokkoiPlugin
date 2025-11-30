package org.meyason.dokkoi.item;

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
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.item.food.*;
import org.meyason.dokkoi.item.jobitem.gacha.*;
import org.meyason.dokkoi.item.goalitem.*;
import org.meyason.dokkoi.item.jobitem.*;
import org.meyason.dokkoi.item.weapon.*;
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.item.battleitems.HealingCrystal;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenuItem;
import org.meyason.dokkoi.item.battleitem.HealingCrystal;

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
        items.put(GameItemKeyString.KILLERLIST, new KillerList());
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
        items.put(GameItemKeyString.TSUYOKUNARU, new Tsuyokunaru());
        items.put(GameItemKeyString.HAYAKUNARU, new Hayakunaru());
        items.put(GameItemKeyString.KIZUKIERU, new Kizukieru());
        items.put(GameItemKeyString.KATAKUNARU, new Katakunaru());
        items.put(GameItemKeyString.KOREHAMARU, new Korehamaru());
        items.put(GameItemKeyString.PUMPKINPIE, new PumpkinPie());
        items.put(GameItemKeyString.COOKEDCHICKEN,new CookedChicken());
        items.put(GameItemKeyString.GLISTERINGMELONSLICE, new GlisteringMelonSlice());
        items.put(GameItemKeyString.COOCKEDPORKCHOP, new CookedPorkchop());
        items.put(GameItemKeyString.COOKEDBEEF, new CookedBeef());
        items.put(GameItemKeyString.COD, new Cod());
        items.put(GameItemKeyString.SALMON, new Salmon());
        items.put(GameItemKeyString.BREAD, new Bread());
        items.put(Skill.id, new Skill());
        items.put(Ultimate.id, new Ultimate());
        items.put(Passive.id, new Passive());
        items.put(KillerList.id, new KillerList());
        items.put(Rapier.id, new Rapier());
        items.put(TierPlayerList.id, new TierPlayerList());
        items.put(Ketsumou.id, new Ketsumou());
        items.put(HealingCrystal.id, new HealingCrystal());
        items.put(BuriBuriGuard.id, new BuriBuriGuard());
        items.put(StrongestBall.id, new StrongestBall());
        items.put(StrongestStrongestBall.id, new StrongestStrongestBall());
        items.put(StrongestStrongestStrongestBall.id, new StrongestStrongestStrongestBall());
        items.put(GoldenCarrot.id, new GoldenCarrot());
        items.put(LongSword.id, new LongSword());
        items.put(ArcherArmor.id, new ArcherArmor());
        items.put(BakedPotato.id, new BakedPotato());
        items.put(ThunderJavelin.id, new ThunderJavelin());
        items.put(RedHelmet.id, new RedHelmet());
        items.put(Tsuyokunaru.id, new Tsuyokunaru());
        items.put(Hayakunaru.id, new Hayakunaru());
        items.put(Kizukieru.id, new Kizukieru());
        items.put(Katakunaru.id, new Katakunaru());
        items.put(Korehamaru.id, new Korehamaru());
        items.put(PumpkinPie.id, new PumpkinPie());
        items.put(CookedChicken.id, new CookedChicken());
        items.put(GoalSelectMenuItem.id, new GoalSelectMenuItem());
    }

    public static CustomItem getItem(String id){
        if(!items.containsKey(id)){
            throw new NoGameItemException("untilized item id: " + id);
        }
        CustomItem item = items.get(id).clone();
        return item;
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
