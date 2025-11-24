package org.meyason.dokkoi.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
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
        if(projectile instanceof Trident trident){
            ProjectileSource projectileSource = trident.getShooter();
            if(projectileSource instanceof Player player){
                if(manager.getPlayerJobs().get(player) instanceof IronMaiden){
                    ProjectileData projectileData = new ProjectileData(player, Rapier.id);
                    manager.addProjectileData(trident, projectileData);
                }
            }

        }
    }
}
