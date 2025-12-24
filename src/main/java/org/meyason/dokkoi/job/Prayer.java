package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.event.player.damage.DamageCalculator;
import org.meyason.dokkoi.event.player.DeathEvent;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.GameLocation;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.item.food.*;
import org.meyason.dokkoi.item.gunitem.*;
import org.meyason.dokkoi.item.jobitem.gacha.*;
import org.meyason.dokkoi.item.utilitem.FortuneBall;
import org.meyason.dokkoi.item.utilitem.IdiotDetector;
import org.meyason.dokkoi.item.utilitem.Monei;
import org.meyason.dokkoi.item.weapon.*;
import org.meyason.dokkoi.util.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;


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
        put(R, List.of(
                CookedChicken.id,
                Cod.id,
                Salmon.id,
                Bread.id,
                BakedPotato.id,
                Arrow.id,
                HealingCrystal.id
        ));
        put(SR, List.of(
                FortuneBall.id,
                NormalBow.id,
                GoldenCarrot.id,
                GlisteringMelonSlice.id,
                CookedBeef.id,
                CookedPorkchop.id,
                PumpkinPie.id,
                Pistol.id,
                LongSword.id,
                ArcherArmor.id,
                HGMagazine.id,
                SMGMagazine.id,
                ARMagazine.id,
                EdenChime.id,
                IdiotDetector.id,
                InstantDevour.id,
                FragGrenade.id
        ));
        put(UR, List.of(
                RedBow.id,
                BlueBow.id,
                Stinger.id,
                DragonBrade.id,
                DrainBrade.id,
                Monei.id
        ));
        put(LR, List.of(
                RailGun.id,
                DrH.id,
                ThunderJavelin.id
        ));
        put(KETSU, List.of(StrongestBall.id));
        put(KETSUGE, List.of(StrongestStrongestBall.id));
        put(KETSUMOU, List.of(StrongestStrongestStrongestBall.id));
    }};

    private List<Location> alreadyOpenedChests = new ArrayList<>();
    public boolean addLocationToAlreadyOpenedChests(Location loc){
        if(alreadyOpenedChests.contains(loc)){return false;}
        alreadyOpenedChests.add(loc);
        return true;
    }

    private int gachaPoint = 0;
    public int getGachaPoint(){
        return gachaPoint;
    }
    public void addGachaPoint(int amount){
        gachaPoint += amount;
    }

    private int gachaCount = 0;
    public int getGachaCount(){
        return gachaCount;
    }

    private boolean hasStrongestStrongestBall = false;
    public boolean getHasStrongestStrongestBall(){
        return hasStrongestStrongestBall;
    }

    public Prayer() {
        super("§9信仰者", "パチカス", 5, 300);

        passive_skill_name += "§7パチンカス";
        normal_skill_name += "§3全回転";
        ultimate_skill_name += "§650:50";

        skillSound = Sound.BLOCK_END_PORTAL_FRAME_FILL;
        skillVolume = 1.0f;
        skillPitch = 1.2f;

        ultimateSkillSound = Sound.ENTITY_ENDER_DRAGON_AMBIENT;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 1.0f;
        setRemainCoolTimeSkillUltimate(300);
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.GACHABEGINNER,
                GoalList.PACHIASU,
                GoalList.GAMBLERMASTER
        );
    }

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§5ガチャ回転数が20回溜まる度に与ダメージが1増加する。"),
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

    public void addGachaCount(Game game, Player player){
        gachaCount++;
        if(gachaCount % 20 == 0){
            game.getGameStatesManager().addAdditionalDamage(player.getUniqueId(), 1);
            player.sendMessage("§bガチャ回転数が20回溜まったので、与ダメージが1増加しました。");
        }
    }

    public void ready(){
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
        if(gachaCount == 49){
            selectedRarity = KETSU;
        }
        List<String> itemList = rarityEffectMap.get(selectedRarity);
        if(itemList == null){
            player.sendMessage("§6エラーが発生しました．管理者に連絡してください：ガチャアイテムリスト取得失敗");
            return;
        }
        String itemName = itemList.get(new Random().nextInt(itemList.size()));
        CustomItem item = null;
        try{
            item = GameItem.getItem(itemName);
        } catch (NoGameItemException e){
            player.sendMessage("§6エラーが発生しました．管理者に連絡してください：ガチャアイテム取得失敗");
            e.printStackTrace();
            return;
        }
        gachaPoint -= 1;
        ItemStack rewardItem = item.getItem();
        PlayerInventory inventory = player.getInventory();
        inventory.addItem(rewardItem);
        player.sendMessage("§aガチャで「" + item.getName() + "§a」を手に入れた！");
        if(Objects.equals(selectedRarity, LR) || Objects.equals(selectedRarity, KETSU) || Objects.equals(selectedRarity, KETSUGE) || Objects.equals(selectedRarity, KETSUMOU)){
            Bukkit.getServer().broadcast((Component.text("§6§l[ガチャ速報] §e" + player.getName() + "§aがガチャで" +selectedRarity+ " " + item.getName() + "§aを引き当てた！")));
        }
        addGachaCount(game, player);

        //レアリティに応じた効果を発動
        if(Objects.equals(selectedRarity, R)) {
            List<Player> targets = CalculateAreaPlayers.getPlayersInArea(game, player, player.getLocation(), 20);
            for(Player target : targets){
                DamageCalculator.calculateSkillDamage(player, target, 1);
            }
        }else if(Objects.equals(selectedRarity, SR)) {
            List<Player> targets = CalculateAreaPlayers.getPlayersInArea(game, player, player.getLocation(), 20);
            for(Player target : targets){
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 2));
            }
        }else if(Objects.equals(selectedRarity, UR)) {
            List<Player> targets = CalculateAreaPlayers.getPlayersInArea(game, player, player.getLocation(), 20);
            for(Player target : targets){
                game.getGameStatesManager().addAdditionalDamage(target.getUniqueId(), -500);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for(Player target : targets){
                        if(!target.isOnline()){
                            continue;
                        }
                        game.getGameStatesManager().addAdditionalDamage(target.getUniqueId(), 500);
                    }
                }
            }.runTaskLater(Dokkoi.getInstance(), 20 * 10);
        }else if(Objects.equals(selectedRarity, LR)) {
            game.getGameStatesManager().addDamageCutPercent(player.getUniqueId(), 100);
            game.getGameStatesManager().addOnDisablePotionEffectPlayer(player.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!player.isOnline()){
                        cancel();
                        return;
                    }
                    game.getGameStatesManager().removeAdditionalDamage(player.getUniqueId());
                    game.getGameStatesManager().addDamageCutPercent(player.getUniqueId(), -100);
                }
            }.runTaskTimer(Dokkoi.getInstance(), 0, 10 * 20);
        }else if(Objects.equals(selectedRarity, KETSU)) {
            player.spawnParticle(Particle.FIREWORK, player.getLocation(), 1, 0.1, 0.1, 0.1);
            List<Player> targets = CalculateAreaPlayers.getPlayersInArea(game, player, player.getLocation(), 20);
            for(Player target : targets){
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 255));
            }
        }else if(Objects.equals(selectedRarity, KETSUGE)) {
            player.spawnParticle(Particle.FIREWORK, player.getLocation(), 3, 1, 1, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            List<Player> targets = CalculateAreaPlayers.getPlayersInArea(game, player, player.getLocation(), 10);
            for(Player target : targets){
                DeathEvent.kill(player, target);
            }
            hasStrongestStrongestBall = true;
        }else if(Objects.equals(selectedRarity, KETSUMOU)) {
            player.spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation(), 5, 1, 1, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0F, 1.0F);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            List<UUID> targets = new ArrayList<>(game.getGameStatesManager().getAlivePlayers());
            for(UUID uuid : targets){
                if(uuid.equals(player.getUniqueId())) continue;
                Player target = Bukkit.getPlayer(uuid);
                if(target == null) continue;
                DeathEvent.kill(player, target);
            }
        }
    }

    public void ultimate(){
        List<UUID> alivePlayerUUID = new ArrayList<>(game.getGameStatesManager().getAlivePlayers());
        alivePlayerUUID.remove(player.getUniqueId());
        if(alivePlayerUUID.isEmpty()){
            player.sendMessage("§c他に生存者がいないため、異空間に転移できませんでした。");
            return;
        }
        UUID uuid = alivePlayerUUID.get(new Random().nextInt(alivePlayerUUID.size()));
        Player target = Bukkit.getPlayer(uuid);
        if(target == null){
            return;
        }
        List<Vector> locations = GameLocation.getInstance().prayerUltimateLocations;
        World world = target.getWorld();
        player.teleport(locations.getFirst().toLocation(world));
        target.teleport(locations.getLast().toLocation(world));
        player.sendMessage("§a" + target.getName() + "§aと異空間に転移しました。どちらかが死ぬまで戻れません。");
        target.sendMessage("§a" + player.getName() + "§aと異空間に転移しました。どちらかが死ぬまで戻れません。");
        player.sendMessage("§a抽選中...");
        target.sendMessage("§a抽選中...");
        GameStatesManager manager = game.getGameStatesManager();
        manager.addDamageCutPercent(player.getUniqueId(), 100);
        manager.addDamageCutPercent(target.getUniqueId(), 100);
        new BukkitRunnable() {
            @Override
            public void run() {
                double result = Math.random();
                if (result < 0.5) {
                    DeathEvent.kill(target, player);
                    player.sendMessage("§aあなたの勝利です。");
                    target.sendMessage("§cあなたは敗北しました。");
                    manager.addDamageCutPercent(player.getUniqueId(), 0);
                } else {
                    DeathEvent.kill(player, target);
                    target.sendMessage("§aあなたの勝利です。");
                    player.sendMessage("§cあなたは敗北しました。");
                    manager.addDamageCutPercent(target.getUniqueId(), 0);
                }
            }
        }.runTaskLater(Dokkoi.getInstance(), 20 * 5);
    }
}
