package org.meyason.dokkoi.event.block;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.event.player.DamageEvent;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
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

        if(entity instanceof Snowball snowball) {
            ProjectileData projectileData = manager.getProjectileDataMap().get(snowball);
            if (projectileData == null) {
                return;
            }

            Player attacker = projectileData.getAttacker();
            String attackItem = projectileData.getCustomItemName();

            Job job = manager.getPlayerJobs().get(attacker);
            if (job instanceof Bomber bomber) {
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), attacker, event.getHitBlock().getLocation(), 1);
                    effectedPlayers.add(attacker);
                    bomber.skill(event.getHitBlock().getLocation(), effectedPlayers);
                }else if(attackItem.equals(GameItemKeyString.ULTIMATE_SKILL)){
                    bomber.ultimate(event.getHitBlock().getLocation());
                }
            }else if(job instanceof Explorer explorer) {
                if(attackItem.equals(GameItemKeyString.SKILL)) {
                    explorer.skill(snowball);
                }
            }
            manager.removeProjectileData(snowball);

        }else if(entity instanceof Trident trident){
            ProjectileData projectileData = manager.getProjectileDataMap().get(trident);
            if (projectileData == null) {
                return;
            }
            trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            if(projectileData.getCustomItemName().equals(Rapier.id)) {
                Player attacker = projectileData.getAttacker();

                Job job = manager.getPlayerJobs().get(attacker);
                if(job instanceof IronMaiden ironMaiden){
                    Rapier rapier = ironMaiden.getRapier();
                    rapier.activate(trident, trident.getLocation());
                }
            }else if(projectileData.getCustomItemName().equals(ThunderJavelin.id)){
                ThunderJavelin.activate(trident);
            }
        }else if(entity instanceof Arrow arrow){
            ProjectileData projectileData = manager.getProjectileDataMap().get(arrow);
            if (projectileData == null) {
                return;
            }

            Player attacker = projectileData.getAttacker();
            if(manager.getPlayerJobs().get(attacker) instanceof Explorer) {
                //自分が放つ矢が着弾した位置に爆発を起こす。爆発は当たった対象に固定10ダメージを与える。
                arrow.getWorld().spawnParticle(Particle.EXPLOSION, arrow.getLocation(), 1);
                arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10.0F, 1.0F);
                List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), attacker, arrow.getLocation(), 3);
                effectedPlayers.add(attacker);
                manager.addAttackedPlayer(attacker);
                for (Player damaged : effectedPlayers) {
                    DamageEvent.calculateDamage(attacker, damaged, 10.0);
                    manager.addDamagedPlayer(damaged);
                }
                manager.removeProjectileData(arrow);
                return;
            }
            manager.removeProjectileData(arrow);
        }
    }
}
