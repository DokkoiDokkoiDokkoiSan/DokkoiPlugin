package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.util.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.jobitem.Ketsumou;

import java.util.List;

public class Explorer extends Job {

    private int haveKetsumouCount = 0;

    private boolean ketsumouMode = false;

    public Explorer() {
        super("冒険者", "冒険者", 5, 200);
        passive_skill_name += "§9§lけつ毛(けつもう)§r§7の力";
        normal_skill_name += "§9§lけつ毛§r§3、投げつけてみた！§d【けつ】§b【KETSU】§a【おしり】§e【山吹権蔵】";
        ultimate_skill_name += "§6爆発するタイプの§9§lけつ毛(けつもう)";

        skillSound = Sound.ENTITY_WARDEN_SONIC_CHARGE;
        skillVolume = 1.0f;
        skillPitch = 0.8f;

        ultimateSkillSound = Sound.BLOCK_SNOW_STEP;
        ultimateSkillVolume = 10.0f;
        ultimateSkillPitch = 1.0f;

        setRemainCoolTimeSkillUltimate(200);
    }

    public boolean isKetsumouMode(){
        return ketsumouMode;
    }

    public int getHaveKetsumouCount(){
        return haveKetsumouCount;
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.KETSUMOUHUNTER,
                GoalList.KETSUMOUPIRATE,
                GoalList.DEFENDER
        );
    }

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§9§lけつ毛§r§5を所持している数によってステータスを獲得する。"),
                Component.text("§a0個　§5与ダメージ0固定、移動速度低下Lv1"),
                Component.text("§a1～3個　§5ステータス変動なし"),
                Component.text("§a4～6個　§5与ダメージが2増加、移動速度上昇Lv1"),
                Component.text("§a7～8個　§5与ダメージが4増加、移動速度上昇Lv1"),
                Component.text("§a9個　§b与ダメージが5増加、移動速度上昇Lv2"),
                Component.text("§5自分が放つ矢が着弾した位置に爆発を起こす。爆発は当たった対象に固定10ダメージを与える。")
        );

        normal_skill_description = List.of(
                Component.text("§9§lけつ毛§r§5を1つ以上所持していないと使用することが出来ない。§9§lけつ毛§r§5を1つ投げる。"),
                Component.text("§9§lけつ毛§r§5が着弾した後、3秒後に着弾位置から半径3m以内にいるプレイヤーは行動不能を5秒間付与される。"),
                Component.text("§5効果が終わった後、§9§lけつ毛§r§5はアイテム化する。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒")
        );

        ultimate_skill_description = List.of(
                Component.text("§5移動速度上昇のバフを20秒間受け取る。"),
                Component.text("§5バフのレベルは自分が持っている§9§lけつ毛§r§5の数×1になる。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    public void ready(){
        passive(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1));
        game.getGameStatesManager().setIsEnableAttack(player.getUniqueId(), false);
    }

    public void passive(int nowCount){
        if(haveKetsumouCount == nowCount){return;}
        if(haveKetsumouCount < nowCount){
            this.haveKetsumouCount = nowCount;
            incrementKetsumouEffect();
        }else {
            this.haveKetsumouCount = nowCount;
            decrementKetsumouEffect();
        }
    }

    public void skill(Snowball snowball){
        // 着弾後の処理
        Location location = snowball.getLocation();
        spawnAreaParticles(location);
        new BukkitRunnable(){
            @Override
            public void run(){
                // 着弾位置から半径3m以内にいるプレイヤーに行動不能を5秒間付与
                List<Player> players = CalculateAreaPlayers.getPlayersInArea(game, player, snowball.getLocation(), 3.0);
                for(Player target : players){
                    if(target.equals(player)) continue;
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 1000));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 1000));
                }
                // けつ毛をアイテム化
                Location location = snowball.getLocation();
                location.setY(location.getY() + 0.1f);
                snowball.remove();

                CustomItem item;
                try{
                    item = GameItem.getItem(Ketsumou.id);
                }catch (NoGameItemException e){
                    e.printStackTrace();
                    cancel();
                    return;
                }
                if(item != null){
                    ItemStack itemStack = item.getItem();
                    location.getWorld().dropItemNaturally(location, itemStack);
                }
            }
        }.runTaskLater(Dokkoi.getInstance(), 3 * 20L);
    }

    public void ultimate(){
        int buffLevel = haveKetsumouCount;
        if(buffLevel == 0) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, buffLevel));
    }

    public void decrementKetsumouEffect(){
        player.sendMessage(Component.text("§9§lけつ毛§r§cの力が弱まった..."));
        // 1から0に、4から3に、7から6に、9から8に減ったとき効果を変化
        if(haveKetsumouCount == 0){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 1));
            game.getGameStatesManager().setIsEnableAttack(player.getUniqueId(), false);
        }else if(haveKetsumouCount == 3){
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            game.getGameStatesManager().addAdditionalDamage(player.getUniqueId(), -2);
        }else if(haveKetsumouCount == 6){
            player.removePotionEffect(PotionEffectType.SPEED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            game.getGameStatesManager().addAdditionalDamage(player.getUniqueId(), -2);
        }else if(haveKetsumouCount == 8) {
            player.removePotionEffect(PotionEffectType.SPEED);
            game.getGameStatesManager().addAdditionalDamage(player.getUniqueId(), -1);
            ketsumouMode = false;
        }
    }

    public void incrementKetsumouEffect(){
        player.sendMessage(Component.text("§9§lけつ毛§r§aの力が強まった！"));
        // 0から1に、3から4に、6から7に、8から9に増えたとき効果を変化
        if(haveKetsumouCount == 1){
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            game.getGameStatesManager().setIsEnableAttack(player.getUniqueId(), true);
        }else if(haveKetsumouCount == 4){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            game.getGameStatesManager().addAdditionalDamage(player.getUniqueId(), 2);
        }else if(haveKetsumouCount == 7){
            game.getGameStatesManager().addAdditionalDamage(player.getUniqueId(), 2);
        }else if(haveKetsumouCount == 9) {
            player.removePotionEffect(PotionEffectType.SPEED);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
            game.getGameStatesManager().addAdditionalDamage(player.getUniqueId(), 1);
            ketsumouMode = true;
        }
    }

    private void spawnAreaParticles(Location impactLocation) {
        long durationTick = 20L * 3; // 5秒
        long intervalTick = 20L;
        Location location = impactLocation.clone();

        Float dragonBreath = 1.0f;

        new BukkitRunnable() {
            long elapsed = 0L;

            @Override
            public void run() {
                if (elapsed >= durationTick) {
                    cancel();
                    return;
                }
                elapsed += intervalTick;

                // 中心を基準に、少なめのパーティクルで広めに見せる
                location.getWorld().spawnParticle(
                        Particle.DRAGON_BREATH,
                        location.getX(), location.getY(), location.getZ(),
                        100,              // 個数を抑える
                        3.0, 1.5, 3.0, 0.1,
                        dragonBreath
                );
            }
        }.runTaskTimer(Dokkoi.getInstance(), 0L, intervalTick);
    }
}
