package org.meyason.dokkoi.item.weapon;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.event.player.DamageEvent;
import org.meyason.dokkoi.game.CalculateAreaPlayers;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class ThunderJavelin extends CustomItem {

    public static final String id = "thunder_javelin";

    public ThunderJavelin() {
        super(id, "雷槍", ItemStack.of(Material.TRIDENT), 1);
        List<Component> lore = List.of(
                Component.text("§5ちょっと前に探検家のおじいちゃん達が別の島で見つけてきたらしい棒。"),
                Component.text("§5大きな鎧さえも破壊できる威力を秘めている。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5投げて使用する。投げて着弾した位置で2秒後に爆発する。"),
                Component.text("§5爆発は半径4m以内のプレイヤーに固定20ダメージを与える。 ")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                item.setItemMeta(meta);
            }
            return item;
        };
    }

    public static void activate(Trident trident){
        Location location = trident.getLocation();
        Player shooter = (Player) trident.getShooter();
        trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        trident.remove();

        new BukkitRunnable(){
            @Override
            public void run() {
                location.getWorld().spawnParticle(Particle.EXPLOSION, location, 1);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 10.0F, 1.0F);
                List<Player> effectedPlayers = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), shooter, location, 4);
                effectedPlayers.add(shooter);
                for (Player damaged : effectedPlayers) {
                    DamageEvent.calculateDamage(shooter, damaged, 20.0);
                    Game.getInstance().getGameStatesManager().addDamagedPlayer(damaged);
                }
            }
        }.runTaskLater(Dokkoi.getInstance(), 2*20L);
    }
}
