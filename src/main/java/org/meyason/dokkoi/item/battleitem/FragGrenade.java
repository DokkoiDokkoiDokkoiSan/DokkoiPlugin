package org.meyason.dokkoi.item.battleitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.event.player.damage.DamageCalculator;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.util.CalculateAreaPlayers;

import java.util.List;

public class FragGrenade extends CustomItem {

    public static final String id = "frag_grenade";

    public FragGrenade() {
        super(id, "§aフラググレネード", ItemStack.of(Material.EGG), 64);
        List<Component> lore = List.of(
                Component.text("§5投げると時間を置いて爆発するグレネード。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5グレネードは投げてから5秒後に爆発する。"),
                Component.text("§5爆発は半径7m以内のプレイヤーに固定20ダメージを与える。"),
                Component.text("§5投げたグレネードが地面にぶつかるとその場に留まる。")
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

    public static void throwGrenade(ProjectileData data) {
        scheduleExplosion(data, 5 * 20);
    }

    public static void scheduleExplosion(ProjectileData data, long delayTicks) {
        Egg egg = (Egg) data.getProjectile();
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                explodeGrenade(data);
            }
        };
        task.runTaskLater(Dokkoi.getInstance(), delayTicks);
        Game.getInstance().getGameStatesManager().addFragGrenadeScheduler(egg, task);
    }

    public static void onHit(ProjectileData projectileData) {
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        Egg egg = (Egg) projectileData.getProjectile();

        // 古いタスクをキャンセル
        BukkitRunnable oldTask = manager.getFragGrenadeScheduler().get(egg);
        if (oldTask != null) {
            oldTask.cancel();
        }

        // 着弾した地点に拾えない卵を設置
        Egg newEgg = egg.getWorld().spawn(egg.getLocation(), Egg.class);
        newEgg.setGravity(false);
        newEgg.setInvulnerable(true);

        // 経過時間を計算して残り時間でタスクを再スケジュール
        long elapsedTicks = projectileData.getElapsedTicks();
        long totalTicks = 5 * 20; // 5秒
        long remainingTicks = Math.max(0, totalTicks - elapsedTicks);

        projectileData.updateProjectile(newEgg);
        manager.addProjectileData(newEgg, projectileData);
        manager.removeFragGrenadeScheduler(egg);
        manager.removeProjectileData(egg);

        egg.remove();

        // 残り時間で新しいタスクをスケジュール
        scheduleExplosion(projectileData, remainingTicks);
    }

    public static void explodeGrenade(ProjectileData data) {
        Egg egg = (Egg) data.getProjectile();
        Player shooter = data.getAttacker();
        Location location = egg.getLocation();
        egg.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, location, 1, 0.0, 0.0, 0.0, 0.0);
        egg.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);

        List<Player> players = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), null, location, 7.0);
        for (Player player : players) {
            DamageCalculator.calculateSkillDamage(shooter, player, 20.0);
        }

        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        manager.removeFragGrenadeScheduler(egg);
        manager.removeProjectileData(egg);
        egg.remove();
    }
}
