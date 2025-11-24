package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.job.Ketsumou;

import java.util.List;

public class Explorer extends Job {

    private int ketsumouCount = 0;

    private boolean ketsumouMode = false;

    public Explorer() {
        super("冒険者", "冒険者", 5, 200);
        passive_skill_name = "けつ毛(けつもう)の力";
        normal_skill_name = "けつ毛、投げつけてみた！【けつ】【KETSU】【おしり】【山吹権蔵】";
        ultimate_skill_name = "爆発するタイプのけつ毛(けつもう)";

        skillSound = Sound.ENTITY_WARDEN_SONIC_CHARGE;
        skillVolume = 1.0f;
        skillPitch = 0.8f;

        ultimateSkillSound = Sound.BLOCK_SNOW_STEP;
        ultimateSkillVolume = 10.0f;
        ultimateSkillPitch = 1.0f;

    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.MASSTIERKILLER,
                GoalList.MAIDENGAZER
        );
    }

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§bけつ毛を所持している数によってステータスを獲得する。"),
                Component.text("§a0個　§b与ダメージ0固定、移動速度低下Lv1が常時付与されている。"),
                Component.text("§a1～3個　§bステータス変動なし。"),
                Component.text("§a4～6個　§b与ダメージが2増加、移動速度上昇Lv1が常時付与されている。"),
                Component.text("§a7～8個　§b与ダメージが4増加、移動速度上昇Lv1が常時付与されている。"),
                Component.text("§a9個　§b与ダメージが5増加、移動速度上昇Lv2が常時付与されている。"),
                Component.text("§b自分が放つ矢が着弾した位置に爆発を起こす。爆発は当たった対象に固定10ダメージを与える。")
        );

        normal_skill_description = List.of(
                Component.text("§bけつ毛を1つ以上所持していないと使用することが出来ない。けつ毛を1つ投げる。"),
                Component.text("§bけつ毛が着弾した後、3秒後に着弾位置から半径3m以内にいるプレイヤーは行動不能を5秒間付与される。"),
                Component.text("§b効果が終わった後、けつ毛はアイテム化する。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒")
        );

        ultimate_skill_description = List.of(
                Component.text("§b移動速度上昇のバフを20秒間受け取る。"),
                Component.text("§bバフのレベルは自分が持っているけつ毛の数×1になる。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    public void ready(){passive();}

    public void passive(){
        int ketsumouCountBefore = ketsumouCount;
        this.ketsumouCount = 0;
        PlayerInventory inventory = player.getInventory();
        for(ItemStack item : inventory.getContents()){
            if(item == null) continue;
            if(item.getItemMeta() != null){
                CustomItem customItem = CustomItem.getItem(item);
                if(customItem instanceof Ketsumou ketsumou){
                    ketsumouCount++;
                }
            }
        }
        if(ketsumouCount == ketsumouCountBefore){return;}
        if(ketsumouCount > ketsumouCountBefore){
            incrementKetsumouEffect();
        }else {
            decrementKetsumouEffect();
        }
    }

    public void skill(Snowball snowball){
        // 着弾後の処理
        new BukkitRunnable(){
            @Override
            public void run(){
                // 着弾位置から半径3m以内にいるプレイヤーに行動不能を5秒間付与
                List<Player> players = CalculateAreaPlayers.getPlayersInArea(game, player, snowball.getLocation(), 3.0);
                for(Player target : players){
                    if(target.equals(player)) continue;
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 10));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 10));
                }
                // けつ毛をアイテム化
                Location location = snowball.getLocation();
                location.setY(location.getY() + 0.1f);
                snowball.remove();

                CustomItem item = GameItem.getItem(Ketsumou.id);
                if(item != null){
                    ItemStack itemStack = item.getItem();
                    location.getWorld().dropItemNaturally(location, itemStack);
                }
            }
        }.runTaskLater(Dokkoi.getInstance(), 3 * 20L);
    }

    public void ultimate(){
        int buffLevel = ketsumouCount;
        if(buffLevel == 0) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, buffLevel));
    }

    public void decrementKetsumouEffect(){
        player.sendMessage(Component.text("§aけつ毛の力が弱まった..."));
        // 1から0に、4から3に、7から6に、9から8に減ったとき効果を変化
        if(ketsumouCount == 0){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1));
            game.getGameStatesManager().addAdditionalDamage(player, -500);
        }else if(ketsumouCount == 3){
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            game.getGameStatesManager().addAdditionalDamage(player, -2);
        }else if(ketsumouCount == 6){
            player.removePotionEffect(PotionEffectType.SPEED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            game.getGameStatesManager().addAdditionalDamage(player, -2);
        }else if(ketsumouCount == 8) {
            player.removePotionEffect(PotionEffectType.SPEED);
            game.getGameStatesManager().addAdditionalDamage(player, -1);
            ketsumouMode = false;
        }
    }

    public void incrementKetsumouEffect(){
        player.sendMessage(Component.text("§aけつ毛の力が強まった！"));
        // 0から1に、3から4に、6から7に、8から9に増えたとき効果を変化
        if(ketsumouCount == 1){
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            game.getGameStatesManager().addAdditionalDamage(player, 500);
        }else if(ketsumouCount == 4){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            game.getGameStatesManager().addAdditionalDamage(player, 2);
        }else if(ketsumouCount == 7){
            game.getGameStatesManager().addAdditionalDamage(player, 2);
        }else if(ketsumouCount == 9) {
            player.removePotionEffect(PotionEffectType.SPEED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
            game.getGameStatesManager().addAdditionalDamage(player, 1);
            ketsumouMode = true;
        }
    }
}
