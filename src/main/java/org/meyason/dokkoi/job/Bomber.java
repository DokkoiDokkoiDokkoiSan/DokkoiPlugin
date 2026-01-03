package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.entity.Comedian;
import org.meyason.dokkoi.event.player.DeathEvent;
import org.meyason.dokkoi.job.context.PassiveContext;
import org.meyason.dokkoi.job.context.SkillContext;
import org.meyason.dokkoi.job.context.UltimateContext;
import org.meyason.dokkoi.job.context.key.Keys;
import org.meyason.dokkoi.util.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;

import java.util.List;
import java.util.UUID;

public class Bomber extends Job {

    public int killCount = 0;

    private int killComedian = 0;

    private boolean isPassiveActive;

    private BukkitTask smokeTask;

    public Bomber() {
        super("爆弾魔", "爆弾のプロ", 20, 100,
                PassiveContext.create(),
                SkillContext.create()
                        .with(Keys.LOCATION, null)
                        .with(Keys.LIST_PLAYER, null),
                UltimateContext.create()
                        .with(Keys.LOCATION, null)
        );
        passive_skill_name += "§7無敵の人";
        normal_skill_name += "§3ブラストパック";
        ultimate_skill_name += "§6割と臭いガス爆弾";
        skillSound = Sound.ENTITY_TNT_PRIMED;
        skillVolume = 1.0f;
        skillPitch = 0.8f;

        ultimateSkillSound = Sound.ENTITY_GENERIC_EXPLODE;
        ultimateSkillVolume = 10.0f;
        ultimateSkillPitch = 1.0f;
        this.isPassiveActive = false;
        setRemainCoolTimeSkillUltimate(100);
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.KILLER,
                GoalList.CARPETBOMBING,
                GoalList.COMEDIANKILLER
        );
    }

    public int getKillComedian(){
        return killComedian;
    }

    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§5HPが0になった瞬間爆発を起こし、半径5m以内にいるプレイヤー、または芸人を巻き込んで自爆する。"),
                Component.text("§5爆発に巻き込まれたプレイヤー・芸人は即死する。プレイヤーか芸人を巻き込んだ場合、その場で復活できる。")
        );

        normal_skill_description = List.of(
                Component.text("§5ダメージの無いノックバック爆弾を投げることができる。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒")
        );

        ultimate_skill_description = List.of(
                Component.text("§5半径20mに煙幕を30秒間発生させる爆弾を投げる。"),
                Component.text("§5煙幕の中では自分は移動速度増加Lv2、相手は移動速度低下Lv1を受ける。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒")
        );
    }

    public boolean isPassiveActive(){
        return this.isPassiveActive;
    }

    public void ready() {
    }

    public void passive(PassiveContext ctx){
        this.isPassiveActive = false;
        this.player.spawnParticle(Particle.EXPLOSION_EMITTER, this.player.getLocation(), 1);
        this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3.0f, 1.0f);
        List<Player> hitPlayer = CalculateAreaPlayers.getPlayersInArea(game, player, player.getLocation(), 5);

        boolean isKilledComedian = false;

        for(Entity entity : player.getNearbyEntities(5, 5, 5)){
            if(entity instanceof Villager villager){
                if(villager.getPersistentDataContainer().isEmpty()) continue;
                String villagerUUID = villager.getPersistentDataContainer().get(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.COMEDIAN), PersistentDataType.STRING);
                if(villagerUUID != null){
                    Comedian comedian = (Comedian) game.getGameStatesManager().getSpawnedEntitiesFromUUID(villagerUUID);
                    game.getGameStatesManager().removeSpawnedEntity(villagerUUID);
                    villager.setHealth(0.0);
                    villager.remove();
                    this.player.sendMessage("§bあなたは芸人 §a" + comedian.getName() + " §bを巻き込んで§l§4自爆§r§aしました");
                    Bukkit.getServer().broadcast(Component.text("§c§l" + comedian.getDeathMessage()));
                    isKilledComedian = true;
                    killComedian++;
                }
            }
        }

        if(hitPlayer.isEmpty() && !isKilledComedian){
            this.player.sendMessage("§cあなたは独りで§l§4自爆§r§cしました");
            return;
        }

        for(Player p : hitPlayer){
            DeathEvent.kill(this.player, p);
            killCount++;
        }

        this.player.setHealth(this.player.getMaxHealth());
        if(!isKilledComedian){
            this.player.sendMessage("§bあなたは§l§4自爆§r§bしましたが、プレイヤーを巻き込んだため復活しました");
        }
        this.isPassiveActive = true;
    }

    public void skill(SkillContext ctx){
        if(!this.getSkillContext().isSatisfiedBy(ctx)){
            throw new IllegalArgumentException("Invalid SkillContext for Bomber skill");
        }
        Location location = ctx.require(Keys.LOCATION);
        List<Player> effectedPlayers = ctx.require(Keys.LIST_PLAYER);
        location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, location, 2);
        location.getWorld().playSound(location, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 10.0f, 1.0f);
        for(Player p : effectedPlayers){
            Vector knockBack = p.getLocation().toVector().subtract(location.toVector()).normalize().multiply(3);
            // Yは抑制
            knockBack.setY(0.1);
            p.setVelocity(knockBack);
        }
    }

    public void ultimate(UltimateContext ctx) {
        if(!this.getSkillContext().isSatisfiedBy(ctx)){
            throw new IllegalArgumentException("Invalid SkillContext for Bomber skill");
        }
        Location impactLocation = ctx.require(Keys.LOCATION);
        if(impactLocation == null){
            throw new IllegalArgumentException("LocationData is required for Bomber skill");
        }
        // パーティクルとサウンド（着弾時だけ一度）
        impactLocation.getWorld().spawnParticle(
                Particle.CAMPFIRE_SIGNAL_SMOKE,
                impactLocation,
                200,
                10, 5, 10, 0.01
        );
        impactLocation.getWorld().playSound(
                impactLocation,
                ultimateSkillSound,
                ultimateSkillVolume,
                ultimateSkillPitch
        );

        // 既存の煙幕タスクがあれば停止
        if (smokeTask != null && !smokeTask.isCancelled()) {
            smokeTask.cancel();
        }

        spawnAreaParticles(impactLocation);

        final double radius = 20.0;
        final long durationTick = 20L * 30; // 30秒
        final long intervalTick = 10L;      // 0.5秒ごとに判定

        long startTick = Bukkit.getCurrentTick();

        // 煙幕タスク開始
        smokeTask = Bukkit.getScheduler().runTaskTimer(
                Dokkoi.getInstance(),       // Game からプラグインインスタンスを取得するメソッドを用意しておく
                () -> {
                    long now = Bukkit.getCurrentTick();
                    if (now - startTick >= durationTick) {
                        // 30秒経過でタスク終了
                        smokeTask.cancel();
                        return;
                    }
                    if(game.getGameStatesManager().getGameState() != GameState.IN_GAME){
                        smokeTask.cancel();
                        return;
                    }

                    List<UUID> targets = game.getGameStatesManager().getAlivePlayers();
                    for (UUID uuid : targets) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p == null) {
                            continue;
                        }
                        if (!p.getWorld().equals(impactLocation.getWorld())) {
                            continue;
                        }
                        if (p.getLocation().distanceSquared(impactLocation) > radius * radius) {
                            continue;
                        }

                        // 常に短い時間で上書きし続ける（ここでは1.5秒）
                        int effectDuration = 30; // tick

                        if (p.getUniqueId().equals(player.getUniqueId())) {
                            // 自分: 移動速度上昇Lv2
                            p.addPotionEffect(new PotionEffect(
                                    PotionEffectType.SPEED,
                                    effectDuration,
                                    1,
                                    false,
                                    false,
                                    true
                            ));
                        } else {
                            // 相手: 移動速度低下Lv1
                            p.addPotionEffect(new PotionEffect(
                                    PotionEffectType.SLOWNESS,
                                    effectDuration,
                                    0,
                                    false,
                                    false,
                                    true
                            ));
                        }
                    }
                },
                0L,
                intervalTick
        );
    }

    private void spawnAreaParticles(Location impactLocation) {
        long durationTick = 20L * 30; // 30秒
        long intervalTick = 20L;      // 0.5秒ごとに発生（重かったら20L=1秒に）

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
                impactLocation.getWorld().spawnParticle(
                        Particle.CAMPFIRE_SIGNAL_SMOKE,
                        impactLocation,
                        100,              // 個数を抑える
                        8, 4, 8,         // x,y,zオフセットで広げる
                        0.01             // extra（0付近でランダム感）
                );
            }
        }.runTaskTimer(Dokkoi.getInstance(), 0L, intervalTick);
    }

}
