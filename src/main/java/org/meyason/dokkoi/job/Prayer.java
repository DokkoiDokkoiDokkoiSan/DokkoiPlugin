package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.job.gacha.GachaMachine;

import java.util.*;

public class Prayer extends Job {

    public static String R = "§f§lR§r";
    public static String SR = "§9§lSR§r";
    public static String UR = "§5§lUR§r";
    public static String LR = "§6§lLR§r";
    public static String KETSU = "§d§lKETSU§r";
    public static String KETSUGE = "§e§lKETSUGE§r";
    public static String KETSUMOU = "§c§lKETSUMOU§r";

    public static double rRate = 0.50;
    public static double srRate = 0.30;
    public static double urRate = 0.15;
    public static double lrRate = 0.04;
    public static double ketsuRate = 0.009;
    public static double ketsugeRate = 0.0009;
    public static double ketsumouRate = 0.0001;

    public static HashMap<String, Double> rateMap = new HashMap<>(){{
        put(R, rRate);
        put(SR, srRate);
        put(UR, urRate);
        put(LR, lrRate);
        put(KETSU, ketsuRate);
        put(KETSUGE, ketsugeRate);
        put(KETSUMOU, ketsumouRate);
    }};

    public static final HashMap<String, List<String>> rarityEffectMap = new HashMap<>(){{
        put(KETSU, List.of(GameItemKeyString.STRONGESTBALL));
        put(KETSUGE, List.of(GameItemKeyString.STRONGESTSTRONGESTBALL));
        put(KETSUMOU, List.of(GameItemKeyString.STRONGESTSTRONGESTSTRONGESTBALL));
    }};

    private List<Location> alreadyOpenedChests = new ArrayList<>();
    public void addLocationToAlreadyOpenedChests(Location loc){
        if(alreadyOpenedChests.contains(loc)){return;}
        alreadyOpenedChests.add(loc);
        gachaPoint++;
    }

    private int gachaPoint = 0;
    public int getGachaPoint(){
        return gachaPoint;
    }

    private int gachaCount = 0;
    public int getGachaCount(){
        return gachaCount;
    }
    public void addGachaCount(Game game, Player player){
        gachaCount++;
        if(gachaCount % 10 == 0){
            game.getGameStatesManager().addAdditionalDamage(player, 1);
            player.setMaxHealth(player.getMaxHealth() + 10);
            player.sendMessage("§bガチャ回転数が10回溜まったので、与ダメージが1と最大体力が10増加しました。");
        }
    }

    public Prayer() {
        super("§9信仰者", "パチカス", 5, 300);

        passive_skill_name = "§7パチンカス";
        normal_skill_name = "§3全回転";
        ultimate_skill_name = "§650:50";

        skillSound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        skillVolume = 1.0f;
        skillPitch = 1.2f;

        ultimateSkillSound = Sound.BLOCK_PORTAL_TRAVEL;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 1.0f;
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.LASTMAN
        );
    }

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§5ガチャ回転数が10回溜まる度に与ダメージが1と最大体力が10増加する。"),
                Component.text("§5開けたことのないチェストを開く度に『ガチャポイント』が貰える。")
        );

        normal_skill_description = List.of(
                Component.text("§5ガチャポイントを消費してガチャガチャを引くことが出来る。"),
                Component.text("§5ガチャガチャからはランダムでアイテムが出てくる。"),
                Component.text("§5また、出たアイテムのレアリティによって周りの敵にデバフを与える。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒"),
                Component.text("§5レアリティ一覧"),
                Component.text("§fR(50%): §2半径20m以内の敵に固定1ダメージ与える。"),
                Component.text("§9SR(30%): §2半径20m以内の敵に移動速度低下Lv2を10秒間与える。"),
                Component.text("§5UR(15%): §2半径20m以内の敵の与ダメージを10秒間1固定にする。"),
                Component.text("§6LR(4%): §2自分が10秒間攻撃、状態異常効果を受けなくなる。"),
                Component.text("§dKETSU(0.9%): §2半径20m以内の敵に行動不能を10秒間与える。"),
                Component.text("§eKETSUGE(0.09%): §2半径10m以内のプレイヤーを即死させる。"),
                Component.text("§cKETSUMOU(0.01%): §2自分以外のプレイヤーを即死させる。")
        );

        ultimate_skill_description = List.of(
                Component.text("§5自分とランダムなプレイヤーを異空間に転移させ、どちらかが死ぬ。"),
                Component.text("§5ここでの死亡は殺害判定に含まれない。")
        );
    }

    public void ready(){
        CustomItem item = GameItem.getItem(GachaMachine.id);
        if(item == null){
            this.player.sendMessage("§6エラーが発生しました．管理者に連絡してください：ガチャマシン取得失敗");
            return;
        }
        ItemStack gachaMachine = item.getItem();
        PlayerInventory inventory = player.getInventory();
        inventory.addItem(gachaMachine);
        this.player.sendMessage("§b右クリックでガチャを回す");
    }

    public void skill(){
        //まずレアリティを決定
        double result = Math.random();
        String selectedRarity = R;
        double cumulativeRate = 0.0;
        for (String rarity : rateMap.keySet()) {
            cumulativeRate += rateMap.get(rarity);
            if (result <= cumulativeRate) {
                selectedRarity = rarity;
                break;
            }
        }
        List<String> itemList = rarityEffectMap.get(selectedRarity);
        if(itemList == null){
            player.sendMessage("§6エラーが発生しました．管理者に連絡してください：ガチャアイテムリスト取得失敗");
            return;
        }
        String itemName = itemList.get(new Random().nextInt(itemList.size()));
        CustomItem item = GameItem.getItem(itemName);
         if(item == null){
              player.sendMessage("§6エラーが発生しました．管理者に連絡してください：ガチャアイテム取得失敗");
              return;
         }
        ItemStack rewardItem = item.getItem();
        PlayerInventory inventory = player.getInventory();
        inventory.addItem(rewardItem);
        player.sendMessage("§aガチャで「" + item.getName() + "§a」を手に入れた！");
        if(Objects.equals(selectedRarity, LR) || Objects.equals(selectedRarity, KETSU) || Objects.equals(selectedRarity, KETSUGE) || Objects.equals(selectedRarity, KETSUMOU)){
            Bukkit.getServer().broadcast((Component.text("§6§l[ガチャ速報] §e" + player.getName() + "§aがガチャで" +selectedRarity+ " " + item.getName() + "§aを引き当てた！")));
        }
        addGachaCount(game, player);
        if(Objects.equals(selectedRarity, R)) {
            List<Player> targets = CalculateAreaPlayers.getPlayersInArea(game, player, player.getLocation(), 20);
        }
    }
}
