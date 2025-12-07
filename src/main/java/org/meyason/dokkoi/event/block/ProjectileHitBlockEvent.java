package org.meyason.dokkoi.event.block;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.event.player.damage.DamageCalculator;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.item.jobitem.Ultimate;
import org.meyason.dokkoi.util.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.jobitem.Rapier;
import org.meyason.dokkoi.item.weapon.ThunderJavelin;
import org.meyason.dokkoi.job.*;

import java.util.List;

public class ProjectileHitBlockEvent implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getHitBlock() == null){
            return;
        }

        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Entity entity = event.getEntity();

        switch (entity) {
            case Snowball snowball -> {
                ProjectileData projectileData = manager.getProjectileDataMap().get(snowball);
                if (projectileData == null) {
                    return;
                }

                Player attacker = projectileData.getAttacker();
                String attackItem = projectileData.getCustomItemName();

                if(manager.isExistGunFromSerial(attackItem)){
                    if (event.getHitBlock().getType().toString().contains("GLASS")) {
                        snowball.getWorld().playSound(snowball.getLocation(), Sound.BLOCK_GLASS_BREAK, 2.0F, 1.0F);
                    }
                    manager.removeProjectileData(snowball);
                    return;
                }

                Job job = manager.getPlayerJobs().get(attacker.getUniqueId());
                if (job instanceof Bomber bomber) {
                    if (attackItem.equals(Skill.id)) {
                        List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), null, event.getHitBlock().getLocation(), 1);
                        bomber.skill(event.getHitBlock().getLocation(), effectedPlayers);
                    } else if (attackItem.equals(Ultimate.id)) {
                        bomber.ultimate(event.getHitBlock().getLocation());
                    }
                } else if (job instanceof Explorer explorer) {
                    if (attackItem.equals(Skill.id)) {
                        explorer.skill(snowball);
                    }
                }
                manager.removeProjectileData(snowball);

            }
            case Trident trident -> {
                ProjectileData projectileData = manager.getProjectileDataMap().get(trident);
                if (projectileData == null) {
                    return;
                }
                trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                if (projectileData.getCustomItemName().equals(Rapier.id)) {
                    Player attacker = projectileData.getAttacker();

                    Job job = manager.getPlayerJobs().get(attacker.getUniqueId());
                    if (job instanceof IronMaiden ironMaiden) {
                        Rapier rapier = ironMaiden.getRapier();
                        rapier.activate(trident, trident.getLocation());
                    }
                } else if (projectileData.getCustomItemName().equals(ThunderJavelin.id)) {
                    ThunderJavelin.activate(trident);
                }
            }
            case Arrow arrow -> {
                ProjectileData projectileData = manager.getProjectileDataMap().get(arrow);
                if (projectileData == null) {
                    return;
                }

                Player attacker = projectileData.getAttacker();
                if (manager.getPlayerJobs().get(attacker.getUniqueId()) instanceof Explorer) {
                    //自分が放つ矢が着弾した位置に爆発を起こす。爆発は当たった対象に固定10ダメージを与える。
                    arrow.getWorld().spawnParticle(Particle.EXPLOSION, arrow.getLocation(), 1);
                    arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10.0F, 1.0F);
                    List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), null, arrow.getLocation(), 3);
                    manager.addAttackedPlayer(attacker.getUniqueId());
                    for (Player damaged : effectedPlayers) {
                        DamageCalculator.calculateSkillDamage(attacker, damaged, 10.0);
                        manager.addDamagedPlayer(damaged.getUniqueId());
                    }
                    manager.removeProjectileData(arrow);
                    return;
                }
                manager.removeProjectileData(arrow);
            }
            default -> {
            }
        }
    }
}
