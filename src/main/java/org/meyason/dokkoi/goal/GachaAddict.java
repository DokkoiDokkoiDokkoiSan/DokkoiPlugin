package org.meyason.dokkoi.goal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.job.gacha.GachaMachine;

import java.util.HashMap;
import java.util.List;

public class GachaAddict extends Goal {

    public static String SSR = "§d§lSSR§r";
    public static String SR = "§a§lSR§r";
    public static String R = "§b§lR§r";

    public static double ssrRate = 0.01;
    public static double srRate = 0.15;
    public static double rRate = 0.84;

    public static HashMap<String, Double> rateMap = new HashMap<String, Double>(){{
        put(SSR, ssrRate);
        put(SR, srRate);
        put(R, rRate);
    }};

    public static final HashMap<Material, Integer> pointMap = new HashMap<Material, Integer>(){{
        put(Material.DIAMOND, 3);
        put(Material.EMERALD, 3);
        put(Material.GOLD_INGOT, 2);
        put(Material.LAPIS_LAZULI, 2);
        put(Material.IRON_INGOT, 1);
        put(Material.REDSTONE, 1);
        put(Material.COAL, 1);
    }};

    public static final HashMap<Material, String> nameMap = new HashMap<Material, String>(){{
        put(Material.DIAMOND, "ダイヤモンド");
        put(Material.GOLD_INGOT, "金のインゴット");
        put(Material.IRON_INGOT, "鉄のインゴット");
        put(Material.LAPIS_LAZULI, "ラピスラズリ");
        put(Material.EMERALD, "エメラルド");
        put(Material.REDSTONE, "レッドストーン");
        put(Material.COAL, "石炭");
        put(Material.BREAD, "パン");
        put(Material.BAKED_POTATO, "ベイクドポテト");
        put(Material.COOKED_BEEF, "ステーキ");
        put(Material.GOLD_NUGGET, "金塊");
        put(Material.WOODEN_SWORD, "木の剣");
        put(Material.IRON_SWORD, "鉄の剣");
        put(Material.IRON_CHESTPLATE, "鉄のチェストプレート");
        put(Material.DIAMOND_SWORD, "ダイヤモンドの剣");
    }};

    public static final int gachaCost = 1;

    private int gachaPoint = 0;
    public int getGachaPoint() {return this.gachaPoint;}
    public void setGachaPoint(int gachaPoint) {this.gachaPoint = gachaPoint;}

    public static final HashMap<Material, String> resultMap = new HashMap<Material, String>(){{
        put(Material.BREAD, R);
        put(Material.BAKED_POTATO, R);
        put(Material.COOKED_BEEF, R);
        put(Material.GOLD_NUGGET, R);
        put(Material.WOODEN_SWORD, R);
        put(Material.IRON_SWORD, SR);
        put(Material.IRON_CHESTPLATE, SR);
        put(Material.IRON_INGOT, SR);
        put(Material.DIAMOND_SWORD, SSR);
    }};

    public GachaAddict() {
        super("GachaAddict", "お前はガチャ中毒だ．SSRを引きあてて生き残れ！ガチャマシンにはアイテムが必要だ．殺してでもアイテムを集めろ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem(){
        this.player.sendMessage("§bアイテムを消費し，§dガチャマシン§bを使用して§cSSR§bを引き当てろ！");
        this.player.sendMessage("§bポイント一覧:\n");
        for(Material material : pointMap.keySet()){
            this.player.sendMessage(" - " + nameMap.get(material) + ": " + pointMap.get(material) + "ポイント\n");
        }
        CustomItem item = GameItem.getItem(GachaMachine.id);
        if(item == null){
            this.player.sendMessage("§6エラーが発生しました．管理者に連絡してください：ガチャマシン取得失敗");
            return;
        }
        ItemStack gachaMachine = item.getItem();
        PlayerInventory inventory = player.getInventory();
        inventory.addItem(gachaMachine);
        this.player.sendMessage("§b左クリックでガチャを回す");
        this.player.sendMessage("§6右クリックでポイント交換メニューを開く");

    }

    @Override
    public boolean isAchieved(){
        if(!game.getGameStatesManager().getAlivePlayers().contains(player)){
            return false;
        }
        PlayerInventory inventory = player.getInventory();
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null) {
                continue;
            }
            Material material = itemStack.getType();

            if (resultMap.containsKey(material) && resultMap.get(material).equals(SSR)) {
                return true;
            }
        }
        return false;
    }

    public static Material getRandomGachaResult(String resultRarity){
        List<Material> materialList = new java.util.ArrayList<>(List.copyOf(resultMap.keySet()));
        resultMap.forEach((material, rarity) -> {
            if(rarity.equals(resultRarity)){
                materialList.add(material);
            }
        });
        int randomIndex = new java.util.Random().nextInt(materialList.size());
        return materialList.get(randomIndex);
    }

}
