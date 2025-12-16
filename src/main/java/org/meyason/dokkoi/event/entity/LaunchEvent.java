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
import org.meyason.dokkoi.item.weapon.BlueBow;
import org.meyason.dokkoi.item.weapon.ThunderJavelin;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.IronMaiden;

public class LaunchEvent implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        Game game = Game.getInstance();
        GameStatesManager manager = game.getGameStatesManager();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        if(projectile.getType() == EntityType.TRIDENT){
            ProjectileSource projectileSource = projectile.getShooter();
            if(projectileSource instanceof Player player){
                if(manager.getPlayerJobs().get(player.getUniqueId()) instanceof IronMaiden ironMaiden){
                    Rapier rapier = ironMaiden.getRapier();
                    player.playSound(player, Sound.ITEM_TRIDENT_RIPTIDE_1, 1.0f, 1.0f);
                    ProjectileData projectileData = new ProjectileData(player, projectile, Rapier.id);
                    manager.addProjectileData(projectile, projectileData);
                }
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                ItemMeta itemMeta = itemInMainHand.getItemMeta();
                if(itemMeta != null){
                    PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                    String itemID = container.get(itemKey, PersistentDataType.STRING);
                    if(itemID != null){
                        if(itemID.equals(ThunderJavelin.id)){
                            ProjectileData projectileData = new ProjectileData(player, projectile, ThunderJavelin.id);
                            manager.addProjectileData(projectile, projectileData);
                            return;
                        }
                    }
                }
            }
        }else if(projectile.getType() == EntityType.ARROW){
            ProjectileSource projectileSource = projectile.getShooter();
            if(projectileSource instanceof Player player){
                if(manager.getPlayerJobs().get(player.getUniqueId()) instanceof Explorer explorer){
                    if(explorer.isKetsumouMode()){
                        ProjectileData projectileData = new ProjectileData(player, projectile, Material.ARROW.name());
                        manager.addProjectileData(projectile, projectileData);
                        return;
                    }
                }
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                ItemMeta itemMeta = itemInMainHand.getItemMeta();
                if(itemMeta != null){
                    PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                    String itemID = container.get(itemKey, PersistentDataType.STRING);
                    if(itemID != null){
                        if(itemID.equals(BlueBow.id)){
                            ProjectileData projectileData = new ProjectileData(player, projectile, BlueBow.id);
                            manager.addProjectileData(projectile, projectileData);
                            return;
                        }
                    }
                }


            }
        }
    }
}
