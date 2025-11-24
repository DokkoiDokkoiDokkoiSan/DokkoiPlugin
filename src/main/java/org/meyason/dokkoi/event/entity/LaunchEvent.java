package org.meyason.dokkoi.event.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.job.Rapier;
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
                    ProjectileData projectileData = new ProjectileData(player, Rapier.id);
                    manager.addProjectileData(projectile, projectileData);
                }
            }

        }
    }
}
