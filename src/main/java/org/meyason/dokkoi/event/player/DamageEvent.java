package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.JobList;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.jobitem.Rapier;
import org.meyason.dokkoi.job.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DamageEvent implements Listener {

    // プレイヤーからプレイヤーへのダメージ
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();
        if(!(attacker instanceof Player attackedPlayer) || !(damaged instanceof Player damagedPlayer)) return;

        if(damagedPlayer.getNoDamageTicks() >= 10){
            event.setCancelled(true);
            return;
        }
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        if(gameStatesManager.getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }

        gameStatesManager.addAttackedPlayer(attackedPlayer);
        gameStatesManager.addDamagedPlayer(damagedPlayer);

        if(gameStatesManager.getPlayerJobs().get(attackedPlayer) instanceof Lonely lonely){
            lonely.lastAttackedTime = System.currentTimeMillis();
        }else if(gameStatesManager.getPlayerJobs().get(damagedPlayer) instanceof Lonely lonely){
            lonely.lastDamagedTime = System.currentTimeMillis();
        }

        if(disableDamageOnce(gameStatesManager, damagedPlayer)){
            return;
        }

        double damage = event.getFinalDamage();
        event.setCancelled(true);
        calculateDamage(attackedPlayer, damagedPlayer, damage);
    }

    // エンティティからのダメージ
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player) return;

        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        Entity entity = event.getEntity();
        if (event.getEntity().isDead()) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        Player attackedPlayer = null;
        Player damagedPlayer = null;
        Entity damagedEntity = event.getEntity();
        double damage = event.getFinalDamage();

        if(damagedEntity instanceof Player dp){
            if(disableDamageOnce(gameStatesManager, dp)){
                return;
            }
        }

        // だいたいのスキル系投擲物はsnowball
        if(event.getDamager() instanceof Snowball snowball) {
            ProjectileData projectileData = gameStatesManager.getProjectileDataMap().get(snowball);
            if (projectileData == null) {
                return;
            }

            attackedPlayer = projectileData.getAttacker();
            if(gameStatesManager.getPlayerJobs().get(attackedPlayer) instanceof Lonely lonely){
                lonely.lastAttackedTime = System.currentTimeMillis();
            }

            Job job = gameStatesManager.getPlayerJobs().get(attackedPlayer);

            // 当たったエンティティがプレイヤーじゃなくてもいい場合はこっち
            if (job instanceof Bomber bomber) {
                String attackItem = projectileData.getCustomItemName();
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), attackedPlayer, snowball.getLocation(), 10);
                    bomber.skill(snowball.getLocation(), effectedPlayers);
                }else if(attackItem.equals(GameItemKeyString.ULTIMATE_SKILL)){
                    bomber.ultimate(snowball.getLocation());
                }
                gameStatesManager.removeProjectileData(snowball);
                return;
            }else if(job instanceof Explorer explorer) {
                String attackItem = projectileData.getCustomItemName();
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    explorer.skill(snowball);
                }
                gameStatesManager.removeProjectileData(snowball);
                return;
            }

            // 当たったエンティティがプレイヤーに限定する効果はこっち
            if(livingEntity instanceof Player damaged) {

                damagedPlayer = damaged;

                gameStatesManager.addAttackedPlayer(attackedPlayer);
                gameStatesManager.addDamagedPlayer(damagedPlayer);

                if(gameStatesManager.getPlayerJobs().get(damagedPlayer) instanceof Lonely lonely){
                    lonely.lastDamagedTime = System.currentTimeMillis();
                }

                if (job instanceof Executor executor) {
                    executor.skill(damagedPlayer);
                }
                gameStatesManager.removeProjectileData(snowball);
                return;
            }

        }else if(event.getDamager() instanceof Trident trident) {
            ProjectileData projectileData = gameStatesManager.getProjectileDataMap().get(trident);
            // 特殊アイテムじゃないばあい(素のトライデント)
            if (projectileData == null) {
                return;
            }
            trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            if(projectileData.getCustomItemName().equals(Rapier.id)) {
                attackedPlayer = projectileData.getAttacker();
                if(gameStatesManager.getPlayerJobs().get(attackedPlayer) instanceof Lonely lonely){
                    lonely.lastAttackedTime = System.currentTimeMillis();
                }

                Job job = gameStatesManager.getPlayerJobs().get(attackedPlayer);
                if(job instanceof IronMaiden ironMaiden){
                    event.setCancelled(true);

                    Rapier rapier = ironMaiden.getRapier();
                    rapier.activate(trident, trident.getLocation());
                    gameStatesManager.removeProjectileData(trident);
                    return;
                }
            }

        }else if(event.getDamager() instanceof Arrow arrow) {
            ProjectileData projectileData = gameStatesManager.getProjectileDataMap().get(arrow);

            if (projectileData == null) {
                ProjectileSource source = arrow.getShooter();
                if(source instanceof Entity shooterEntity){
                    if(shooterEntity instanceof Player attackerPlayer){
                        if(gameStatesManager.getPlayerJobs().get(attackerPlayer) instanceof Lonely lonely){
                            lonely.lastAttackedTime = System.currentTimeMillis();
                        }
                    }
                    if(damagedEntity instanceof Player damagedP){
                        if(gameStatesManager.getPlayerJobs().get(damagedP) instanceof Lonely lonely){
                            lonely.lastDamagedTime = System.currentTimeMillis();
                        }
                    }
                    calculateDamage(shooterEntity, damagedEntity, damage);
                }
                return;
            }

            attackedPlayer = projectileData.getAttacker();
            if(gameStatesManager.getPlayerJobs().get(attackedPlayer) instanceof Explorer) {
                //自分が放つ矢が着弾した位置に爆発を起こす。爆発は当たった対象に固定10ダメージを与える。
                arrow.getWorld().spawnParticle(Particle.EXPLOSION, arrow.getLocation(), 1);
                arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10.0F, 1.0F);
                List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), attackedPlayer, arrow.getLocation(), 3);
                effectedPlayers.add(attackedPlayer);
                gameStatesManager.addAttackedPlayer(attackedPlayer);
                for (Player damaged : effectedPlayers) {
                    gameStatesManager.addDamagedPlayer(damaged);
                }
                gameStatesManager.removeProjectileData(arrow);
                return;
            }
            gameStatesManager.removeProjectileData(arrow);
        }

        calculateDamage(attackedPlayer, damagedPlayer, damage);

    }

    //　プレイヤーからエンティティへのダメージ
    @EventHandler
    public static void onEntityDamage(EntityDamageByEntityEvent event){
        Entity damaged = event.getEntity();
        if(damaged instanceof Player){return;}
        if(!(event.getDamager() instanceof Player attacker)){return;}
        if(damaged.isDead()){
            return;
        }
        Game game = Game.getInstance();
        GameStatesManager gameStatesManager = game.getGameStatesManager();
        if(gameStatesManager.getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }

        if(gameStatesManager.getPlayerJobs().get(attacker) instanceof Lonely lonely){
            lonely.lastAttackedTime = System.currentTimeMillis();
        }
    }

    @EventHandler
    public static void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player player){
            Game game = Game.getInstance();
            GameStatesManager gameStatesManager = game.getGameStatesManager();
            if(gameStatesManager.getGameState() != GameState.IN_GAME) {
                event.setCancelled(true);
                return;
            }

            EntityDamageEvent.DamageCause cause = Optional.ofNullable(event.getEntity().getLastDamageCause())
                    .map(EntityDamageEvent::getCause)
                    .orElse(null);
            if(cause == EntityDamageEvent.DamageCause.FALL){
                event.setCancelled(true);
            }
        }
    }

    public static void calculateDamage(Entity attacker, Entity damaged, double damage){
        if(attacker == null || damaged == null) {
            return;
        }
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        if(damaged instanceof Player damagedPlayer && attacker instanceof Player attackerPlayer) {
            double additionalDamage = gameStatesManager.getAdditionalDamage().get(attackerPlayer);
            if (additionalDamage <= -300) {
                damage = 1.0;
            } else {
                damage += additionalDamage;
            }

            damage *= gameStatesManager.getPlayerGoals().get(damaged).getDamageMultiplier();
            if (gameStatesManager.getKillerList().containsKey(attackerPlayer) && gameStatesManager.getPlayerJobs().get(damaged).equals(JobList.EXECUTOR)) {
                damage /= 2.0;
            }

            int damageCutPercent = gameStatesManager.getDamageCutPercent().get(damaged);
            damage = damage * (100 - damageCutPercent) / 100.0;

            if (damage < 0) {
                return;
            }

            double afterHealth = damagedPlayer.getHealth() - damage;
            // 死亡処理
            if (afterHealth <= 0) {
                DeathEvent.kill(attackerPlayer, damagedPlayer);
            }else{
                damagedPlayer.damage(damage);
//                damagedPlayer.setHealth(afterHealth);
            }
        }
    }

    public static boolean disableDamageOnce(GameStatesManager manager, Player player){
        if(manager.getIsDeactivateDamageOnce().get(player)){
            ItemStack item = player.getInventory().getChestplate();
            if(item != null){
                if(!item.hasItemMeta()){return true;}
                ItemMeta meta = item.getItemMeta();
                if(meta != null){
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
                    if(container.has(itemKey, PersistentDataType.STRING)){
                        if(Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.ARCHERARMOR)){
                            player.getInventory().setChestplate(null);
                            player.sendMessage(Component.text("§a弓使いの鎧§bの効果が発動した！"));
                        }
                    }
                }
            }else{
                player.sendMessage(Component.text("§aカタクナール§bの効果が発動した！"));
            }
            manager.addIsDeactivateDamageOnce(player, false);
            return true;
        }
        return false;
    }
}
