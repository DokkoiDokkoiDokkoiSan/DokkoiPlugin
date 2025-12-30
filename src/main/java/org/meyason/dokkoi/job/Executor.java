package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.entity.Dealer;
import org.meyason.dokkoi.entity.GameEntity;
import org.meyason.dokkoi.event.player.damage.DamageCalculator;
import org.meyason.dokkoi.event.player.DeathEvent;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;

import java.util.List;
import java.util.UUID;

public class Executor extends Job{

    private int arrestCount = 0;
    public int getArrestCount() {
        return arrestCount;
    }

    public Executor() {
        super("執行者", "執行者", 30, 200);
        passive_skill_name += "§7プロトペナルティ";
        normal_skill_name += "§3ギルトペナルティ";
        ultimate_skill_name += "§6ニクトペナルティ";
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
                GoalList.createPolice(),
                GoalList.createDefender(),
                GoalList.createDrugEnforcementAdministration()
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

    public void skill(Entity target){
        if(target instanceof Player targetPlayer) {
            int killCount = game.getGameStatesManager().getKillCounts().get(player.getUniqueId());
            int damage;
            if (killCount == 0) {
                damage = 5;
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, 1));
            } else if (killCount == 1) {
                damage = 20;
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 2));
                game.getGameStatesManager().addAdditionalDamage(targetPlayer.getUniqueId(), -2);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        game.getGameStatesManager().addAdditionalDamage(targetPlayer.getUniqueId(), 2);
                    }
                }.runTaskLater(Dokkoi.getInstance(), 5 * 20L);
            } else if (killCount == 2) {
                damage = 40;
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 1));
                targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 1));
                game.getGameStatesManager().addAdditionalDamage(targetPlayer.getUniqueId(), -5);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        game.getGameStatesManager().addAdditionalDamage(targetPlayer.getUniqueId(), 5);
                    }
                }.runTaskLater(Dokkoi.getInstance(), 10 * 20L);
            } else {
                damage = 500;
            }
            DamageCalculator.calculateSkillDamage(player, targetPlayer, damage);

        }else if(target instanceof Villager villager){
            NamespacedKey npcKey = new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.NPC);
            if(villager.getPersistentDataContainer().has(npcKey)) {
                String npcUUID = villager.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
                if(npcUUID != null) {
                    GameEntity gameEntity = game.getGameStatesManager().getSpawnedEntitiesFromUUID(npcUUID);
                    if(gameEntity instanceof Dealer dealer){
                        dealer.arrested(player);
                        game.getGameStatesManager().removeSpawnedEntity(npcUUID);
                        villager.remove();
                        arrestCount += 1;
                    }
                }
            }
        }
    }

    public void ultimate(){
        if(!game.getGameStatesManager().getAttackedPlayers().isEmpty()){
            for(UUID uuid : game.getGameStatesManager().getAttackedPlayers()){
                Player target = Bukkit.getPlayer(uuid);
                if(target == null) continue;
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
