package org.meyason.dokkoi.item.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;

public class Rapier extends CustomItem {

    public static final String id = "rapier";

    private Game game;
    private Player player;

    public Rapier(){
        super(id, "レイピア", ItemStack.of(Material.TRIDENT));
        isUnique = true;
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
        player.sendMessage(Component.text("§bレイピアを手に入れた！"));
    }

    public void activate(Location loc){
        if (loc == null || loc.getWorld() == null) {
            return;
        }

        final Location hitLocation = loc.clone();
        final World world = hitLocation.getWorld();
        final double radius = 10.0;

        // 半径10m以内のプレイヤーの視線を着弾地点に固定
        BukkitRunnable rapierTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!game.getGameStatesManager().getGameState().equals(GameState.IN_GAME)) {
                    this.cancel();
                    return;
                }

                for (Player target : world.getPlayers()) {
                    if (target.equals(player)) {
                        continue;
                    }

                    if (target.getLocation().distanceSquared(hitLocation) > radius * radius) {
                        continue;
                    }

                    // 現在位置を基準に向きだけを着弾地点へ向ける
                    Location targetLoc = target.getLocation().clone();
                    targetLoc.setDirection(
                            hitLocation.toVector().subtract(targetLoc.toVector())
                    );
                    target.teleport(targetLoc);
                    target.sendMessage(Component.text("§c[鉄処女]あれ見てみろ！かす！"));
                }
            }
        };

        rapierTask.runTaskTimer(Dokkoi.getInstance(), 0, 10);
    }
}
