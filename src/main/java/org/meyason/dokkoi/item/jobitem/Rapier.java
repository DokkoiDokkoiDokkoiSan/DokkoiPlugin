package org.meyason.dokkoi.item.jobitem;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.job.IronMaiden;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Rapier extends CustomItem {

    public static final String id = "rapier";

    private Game game;
    private Player player;

    public Rapier(){
        super(id, "§aレイピア", ItemStack.of(Material.TRIDENT), 1);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5割とかっこいいレイピア、なんか先端クッソ白くね？"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5投げて着弾した位置から半径10m以内にいるプレイヤーの視線をレイピアに固定し続ける。"),
                Component.text("§5自然消滅はしない。")
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

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        player.sendMessage(Component.text("§aレイピア§bを手に入れた！"));
    }

    public void activate(Trident trident, Location loc){
        if (loc == null || loc.getWorld() == null) {
            return;
        }

        final Location hitLocation = loc.clone();
        final World world = hitLocation.getWorld();
        final double radius = 10.0;

        IronMaiden ironMaidenJob = (IronMaiden) game.getGameStatesManager().getPlayerJobs().get(player);

        // 半径10m以内のプレイヤーの視線を着弾地点に固定
        BukkitRunnable rapierTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!game.getGameStatesManager().getGameState().equals(GameState.IN_GAME)) {
                    trident.remove();
                    this.cancel();
                    return;
                }
                boolean isSuccess = false;
                for(Entity entity : world.getNearbyEntities(hitLocation, radius, radius, radius)){
                    if (entity instanceof Player target) {
                        if (target.equals(player)) {
                            continue;
                        }
                        if(target.getGameMode() == GameMode.SPECTATOR){
                            continue;
                        }

                        // 現在位置を基準に向きだけを着弾地点へ向ける
                        Location targetLoc = target.getEyeLocation().clone();
                        targetLoc.setDirection(
                                hitLocation.toVector().subtract(targetLoc.toVector())
                        );
                        targetLoc.setY(target.getY());
                        target.teleport(targetLoc);
                        target.sendActionBar(Component.text("§c[鉄処女]あれ見てみろ！かす！"));
                        isSuccess = true;
                    }
                }

                if(isSuccess){
                    ironMaidenJob.addCount();
                }
            }
        };

        rapierTask.runTaskTimer(Dokkoi.getInstance(), 0, 10);
    }
}
