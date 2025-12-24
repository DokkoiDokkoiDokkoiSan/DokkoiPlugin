package org.meyason.dokkoi.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.exception.FailChestRandomItemException;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.item.food.*;
import org.meyason.dokkoi.item.gunitem.*;
import org.meyason.dokkoi.item.jobitem.Ketsumou;
import org.meyason.dokkoi.item.utilitem.*;
import org.meyason.dokkoi.item.weapon.*;
import org.meyason.dokkoi.scheduler.ChestScheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ChestProvider {

    private static ChestProvider instance;

    public static double rRate = 0.60;
    public static double srRate = 0.35;
    public static double ssrRate = 0.045;
    public static double urRate = 0.005;

    public static String R = "R";
    public static String SR = "SR";
    public static String SSR = "SSR";
    public static String UR = "UR";

    private boolean isPopMamiya = false;
    private boolean isPopTakashima = false;

    private boolean isPopThunderJavelin = false;
    private boolean isPopRailGun = false;

    private int maxKetsumouCount = 9;

    private int currentKetsumouCount = 0;

    private BukkitTask task;

    public static HashMap<String, Double> rateMap = new HashMap<>(){{
        put(R, rRate);
        put(SR, srRate);
        put(UR, urRate);
        put(SSR, ssrRate);
    }};

    public static final HashMap<String, List<String>> rarityEffectMap = new HashMap<>(){{
        put(R, List.of(
                CookedChicken.id,
                Cod.id,
                Salmon.id,
                Bread.id,
                BakedPotato.id,
                GoldenCarrot.id,
                GlisteringMelonSlice.id,
                CookedBeef.id,
                CookedPorkchop.id,
                PumpkinPie.id,
                Ketsumou.id
        ));
        put(SR, List.of(
                HealingCrystal.id,
                ArcherArmor.id,
                IdiotDetector.id,
                InstantDevour.id,
                MamiyaPhone.id,
                TakashimaPhone.id,
                FragGrenade.id
        ));
        put(SSR, List.of(
                NormalBow.id,
                HGMagazine.id,
                SMGMagazine.id,
                ARMagazine.id,
                Pistol.id,
                LongSword.id,
                FortuneBall.id,
                EdenChime.id
        ));
        put(UR, List.of(
                RailGun.id,
                ThunderJavelin.id
        ));
    }};

    public ChestProvider(){
        instance = this;
        this.task = null;
    }

    public static ChestProvider getInstance() {
        if (instance == null) {
            instance = new ChestProvider();
        }
        return instance;
    }

    public ItemStack getRandomItem(){
        Random rand = new Random();
        double p = rand.nextDouble();
        double cumulativeProbability = 0.0;
        String selectedRarity = R; // Default rarity
        for (String rarity : rateMap.keySet()) {
            cumulativeProbability += rateMap.get(rarity);
            if (p <= cumulativeProbability) {
                selectedRarity = rarity;
                break;
            }
        }
        List<String> possibleItems = rarityEffectMap.get(selectedRarity);
        String selectedItemId = possibleItems.get(rand.nextInt(possibleItems.size()));
        CustomItem customItem;
        try {
            customItem = GameItem.getItem(selectedItemId);
        } catch (NoGameItemException e) {
            e.printStackTrace();
            throw new FailChestRandomItemException("チェストのランダムアイテムの取得に失敗しました。");
        }
        if(customItem instanceof ThunderJavelin){
            if(isPopThunderJavelin){
                return getRandomItem();
            }else{
                isPopThunderJavelin = true;
            }
        } else if (customItem instanceof MamiyaPhone){
            if(isPopMamiya){
                return getRandomItem();
            }else{
                isPopMamiya = true;
            }
        } else if (customItem instanceof TakashimaPhone){
            if(isPopTakashima){
                return getRandomItem();
            }else{
                isPopTakashima = true;
            }
        } else if (customItem instanceof RailGun){
            if(isPopRailGun){
                return getRandomItem();
            }else{
                isPopRailGun = true;
            }
        }else if (customItem instanceof Ketsumou){
            if(currentKetsumouCount >= maxKetsumouCount){
                return getRandomItem();
            }else {
                currentKetsumouCount += 1;
            }
        }
        ItemStack itemStack = customItem.getItem();
        return itemStack;
    }

    public void startTask(){
        task = new ChestScheduler().runTaskTimer(Dokkoi.getInstance(), 0L, 20 * 20);
    }

    public void cancelTask(){
        if(task != null) {
            isPopMamiya = false;
            isPopTakashima = false;
            isPopThunderJavelin = false;
            isPopRailGun = false;
            task.cancel();
        }
    }

    public static void removeAllChests() {
        for(Vector vector : GameLocation.getInstance().chestLocations) {
            Location loc = new Location(
                    Bukkit.getWorld("world"),
                    vector.getX(),
                    vector.getY(),
                    vector.getZ()
            );
            Block block = Objects.requireNonNull(Bukkit.getWorld("world")).getBlockAt(loc);
            if(block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST){
                Chest chest = (Chest) block.getState();
                Inventory inventory = chest.getInventory();
                inventory.clear();
            }
        }
    }
}
