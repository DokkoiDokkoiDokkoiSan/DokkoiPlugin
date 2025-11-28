package org.meyason.dokkoi.event.entity;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.jobitem.Rapier;
import org.meyason.dokkoi.item.weapon.ThunderJavelin;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.IronMaiden;

public class LaunchEvent implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
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
                CustomItem customItem = CustomItem.getItem(player.getInventory().getItemInMainHand());
                if(customItem != null){
                    if(customItem instanceof ThunderJavelin){
                        ProjectileData projectileData = new ProjectileData(player, ThunderJavelin.id);
                        manager.addProjectileData(projectile, projectileData);
                    }
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
