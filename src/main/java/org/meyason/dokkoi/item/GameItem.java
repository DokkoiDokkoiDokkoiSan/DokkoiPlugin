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
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.item.debug.Debug;
import org.meyason.dokkoi.item.food.*;
import org.meyason.dokkoi.item.gunitem.*;
import org.meyason.dokkoi.item.jobitem.gacha.*;
import org.meyason.dokkoi.item.goalitem.*;
import org.meyason.dokkoi.item.jobitem.*;
import org.meyason.dokkoi.item.matching.*;
import org.meyason.dokkoi.item.utilitem.*;
import org.meyason.dokkoi.item.weapon.*;
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenuItem;

import java.util.HashMap;

public class GameItem {

    private static HashMap<String, CustomItem> items = new HashMap<>();

    public GameItem(){
        registerItem();
    }

    public void registerItem(){
        items.put(Skill.id, new Skill());
        items.put(Ultimate.id, new Ultimate());
        items.put(Passive.id, new Passive());
        items.put(KillerList.id, new KillerList());
        items.put(UnkillerList.id, new UnkillerList());
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
        items.put(Arrow.id, new Arrow());
        items.put(ArcherArmor.id, new ArcherArmor());
        items.put(BakedPotato.id, new BakedPotato());
        items.put(ThunderJavelin.id, new ThunderJavelin());
        items.put(RedHelmet.id, new RedHelmet());
        items.put(Tsuyokunaru.id, new Tsuyokunaru());
        items.put(Hayakunaru.id, new Hayakunaru());
        items.put(Kizukieru.id, new Kizukieru());
        items.put(Katakunaru.id, new Katakunaru());
        items.put(Korehamaru.id, new Korehamaru());
        items.put(TotemoTsuyokunaru.id, new TotemoTsuyokunaru());
        items.put(TotemoKorehamaru.id, new TotemoKorehamaru());
        items.put(TotemoHayakunaru.id, new TotemoHayakunaru());
        items.put(TotemoKatakunaru.id, new TotemoKatakunaru());
        items.put(TotemoKizukieru.id, new TotemoKizukieru());
        items.put(DrugRecipe.id, new DrugRecipe());
        items.put(PumpkinPie.id, new PumpkinPie());
        items.put(CookedChicken.id, new CookedChicken());
        items.put(GoalSelectMenuItem.id, new GoalSelectMenuItem());
        items.put(GlisteringMelonSlice.id, new GlisteringMelonSlice());
        items.put(CookedBeef.id, new CookedBeef());
        items.put(CookedPorkchop.id, new CookedPorkchop());
        items.put(Cod.id, new Cod());
        items.put(Salmon.id, new Salmon());
        items.put(Bread.id, new Bread());
        items.put(Monei.id, new Monei());
        items.put(Debug.id, new Debug());
        items.put(TakashimaPhone.id, new TakashimaPhone());
        items.put(MamiyaPhone.id, new MamiyaPhone());
        items.put(InstantDevour.id, new InstantDevour());
        items.put(Hikakin.id, new Hikakin());
        items.put(SummonersBrave.id, new SummonersBrave());
        items.put(Pistol.id, new Pistol());
        items.put(Stinger.id, new Stinger());
        items.put(DrH.id, new DrH());
        items.put(RailGun.id, new RailGun());
        items.put(HGMagazine.id, new HGMagazine());
        items.put(SMGMagazine.id, new SMGMagazine());
        items.put(ARMagazine.id, new ARMagazine());
        items.put(PotionBottleFull.id, new PotionBottleFull());
        items.put(PotionBottleEmpty.id, new PotionBottleEmpty());
        items.put(NormalBow.id, new NormalBow());
        items.put(RedBow.id, new RedBow());
        items.put(BlueBow.id, new BlueBow());
        items.put(DragonBrade.id, new DragonBrade());
        items.put(DrainBrade.id, new DrainBrade());
        items.put(IdiotDetector.id, new IdiotDetector());
        items.put(FortuneBall.id, new FortuneBall());
        items.put(FragGrenade.id, new FragGrenade());
        items.put(EdenChime.id, new EdenChime());
        items.put(JoinQueueItem.id, new JoinQueueItem());
        items.put(QuitQueueItem.id, new QuitQueueItem());
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
