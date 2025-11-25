package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.event.player.DeathEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.scheduler.SkillScheduler;

import java.util.List;

public class Executor extends Job{

    public Executor() {
        super("§9執行者", "執行者", 30, 200);
        passive_skill_name = "§7プロトペナルティ";
        normal_skill_name = "§3ギルトペナルティ";
        ultimate_skill_name = "§6ニクトペナルティ";
        skillSound = Sound.ENTITY_PHANTOM_BITE;
        skillVolume = 1.0f;
        skillPitch = 0.8f;

        ultimateSkillSound = Sound.BLOCK_ANVIL_PLACE;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 0.6f;
        setRemainCoolTimeSkillUltimate(200);
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.POLICE,
                GoalList.DEFENDER
        );
    }

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(Component.text("§5プレイヤーを一人でも殺したプレイヤーから受けるダメージが半分になる。"));

        normal_skill_description = List.of(
                Component.text("§5スキルが命中したプレイヤーのキル数に応じてダメージ、デバフを与える。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒"),
                Component.text("§a0kill §5固定5ダメージ与え、3秒間の§1鈍足"),
                Component.text("§a1kill §5固定20ダメージ与え、5秒間の§1鈍足§5、与ダメージ2減少"),
                Component.text("§a2kill §5固定40ダメージ与え、10秒間の§1鈍足§5、与ダメージ5減少、§3盲目"),
                Component.text("§a3kill §5固定500ダメージ与える。")
        );

        ultimate_skill_description = List.of(
                Component.text("§5自身の移動速度が10秒間4倍になる。"),
                Component.text("§5試合中一度でも攻撃を行ったプレイヤーに10秒間の§1鈍足§5と§3盲目§5のデバフを与える。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    public void ready(){}

    public void skill(Player target){
        Integer value = game.getGameStatesManager().getKillCounts().get(player);
        int killCount = value.intValue();
        int damage;
        if(killCount == 0){
            damage = 5;
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, 1));
        }else if(killCount == 1){
            damage = 20;
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 2));
            game.getGameStatesManager().addAdditionalDamage(target, -2);
            new BukkitRunnable() {
                @Override
                public void run() {
                    game.getGameStatesManager().addAdditionalDamage(target, 2);
                }
            }.runTaskLater(Dokkoi.getInstance(), 5 * 20L);
        }else if(killCount == 2){
            damage = 40;
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 1));
            game.getGameStatesManager().addAdditionalDamage(target, -5);
            new BukkitRunnable() {
                @Override
                public void run() {
                    game.getGameStatesManager().addAdditionalDamage(target, 5);
                }
            }.runTaskLater(Dokkoi.getInstance(), 10 * 20L);
        }else{
            damage = 500;
        }
        if(damage < target.getHealth()){
            target.setHealth(target.getHealth() - damage);
        }else{
            DeathEvent.kill(player, target);
        }
    }

    public void ultimate(){
        if(!game.getGameStatesManager().getAttackedPlayers().isEmpty()){
            for(Player target : game.getGameStatesManager().getAttackedPlayers()){
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 3));
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 3));
            }
        }
        this.player.setWalkSpeed(0.8f);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(0.2f);
            }
        }.runTaskLater(Dokkoi.getInstance(), 20L * 10L);
    }
}
