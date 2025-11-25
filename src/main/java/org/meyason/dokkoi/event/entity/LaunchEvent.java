package org.meyason.dokkoi.event.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.job.Rapier;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.IronMaiden;

public class LaunchEvent implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        // イベント処理のコードをここに記述
        Projectile projectile = event.getEntity();
        Game game = Game.getInstance();
        GameStatesManager manager = game.getGameStatesManager();
        if(projectile.getType() == EntityType.TRIDENT){
            ProjectileSource projectileSource = projectile.getShooter();
            if(projectileSource instanceof Player player){
                if(manager.getPlayerJobs().get(player) instanceof IronMaiden){
                    player.playSound(player, Sound.ITEM_TRIDENT_RIPTIDE_1, 1.0f, 1.0f);
                    ProjectileData projectileData = new ProjectileData(player, Rapier.id);
                    manager.addProjectileData(projectile, projectileData);
                }
            }
        }else if(projectile.getType() == EntityType.ARROW){
            ProjectileSource projectileSource = projectile.getShooter();
            if(projectileSource instanceof Player player){
                if(manager.getPlayerJobs().get(player) instanceof Explorer explorer){
                    if(explorer.isKetsumouMode()){
                        ProjectileData projectileData = new ProjectileData(player, Material.ARROW.name());
                        manager.addProjectileData(projectile, projectileData);
                    }
                }
            }
        }
    }
}
