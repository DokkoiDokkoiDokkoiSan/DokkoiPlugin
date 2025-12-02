package org.meyason.dokkoi.game;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.exception.FailChestRandomItemException;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.item.food.*;
import org.meyason.dokkoi.item.weapon.*;
import org.meyason.dokkoi.scheduler.ChestScheduler;

import java.util.HashMap;
import java.util.List;
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

    private BukkitTask task;

    public static HashMap<String, Double> rateMap = new HashMap<>(){{
        put(R, rRate);
        put(SR, srRate);
        put(UR, urRate);
        put(SSR, ssrRate);
    }};

    public static final HashMap<String, List<String>> rarityEffectMap = new HashMap<>(){{
        put(R, List.of(
                BakedPotato.id,
                Arrow.id,
                Cod.id,
                Bread.id,
                HealingCrystal.id,
                Salmon.id,
                CookedChicken.id,
                GoldenCarrot.id,
                CookedPorkchop.id,
                CookedBeef.id,
                GlisteringMelonSlice.id,
                PumpkinPie.id
        ));
        put(SR, List.of(
                HealingCrystal.id,
                ArcherArmor.id
        ));
        put(SSR, List.of(
                LongSword.id
        ));
        put(UR, List.of(
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
                // 2個目以降は出さない
                return getRandomItem();
            }else{
                isPopThunderJavelin = true;
            }
        }
        ItemStack itemStack = customItem.getItem();
        return itemStack;
    }

    public void startTask(){
        task = new ChestScheduler().runTaskTimer(Dokkoi.getInstance(), 0L, 20 * 20);
    }

    public void cancelTask(){
        if(task != null) task.cancel();
    }
}
