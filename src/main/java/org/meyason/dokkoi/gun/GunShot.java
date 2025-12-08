package org.meyason.dokkoi.gun;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.item.gunitem.GunItem;

import java.util.Random;

public class GunShot {

    private Snowball projectile;
    public Snowball getProjectile() {return projectile;}
    private Player player;
    private Vector direction;
    private GunItem gun;
    public Location getLaunchedLocation() {
        return player.getEyeLocation();
    }

    public GunShot(Player player, GunItem gun, GunStatus gunStatus){
        this.player = player;
        this.gun = gun;
        gunStatus.updateActionBar(player);
        direction = player.getEyeLocation().getDirection().normalize();
        SpreadProjectile();
        Vector velocity = direction.multiply(gun.getBulletSpeed());
        projectile = player.launchProjectile(Snowball.class, velocity);
        ItemStack itemStack = new ItemStack(Material.SNOWBALL);
        projectile.setItem(itemStack);
        projectile.setGravity(false);
        ShotEffect();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!projectile.isDead()) {
                    projectile.remove();
                }
            }
        }.runTaskLater(Dokkoi.getInstance(), 40);
    }


    private void SpreadProjectile() {
        double spread = gun.getSpread();
        spread /= 5.0;
        Random random = new Random();
        double spreadX = random.nextGaussian() * spread;
        double spreadY = random.nextGaussian() * spread;
        double spreadZ = random.nextGaussian() * spread;
        direction.add(new Vector(spreadX, spreadY, spreadZ)).normalize();
    }

    public void ShotEffect() {
        player.getWorld().playSound(player.getLocation(), gun.getShotSound(), gun.getVolume(), gun.getPitch());
    }

}
