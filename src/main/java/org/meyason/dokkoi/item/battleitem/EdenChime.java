package org.meyason.dokkoi.item.battleitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;
import org.meyason.dokkoi.item.itemhooker.InventoryHooker;
import org.meyason.dokkoi.util.CalculateAreaPlayers;

import java.util.List;
import java.util.UUID;

public class EdenChime extends CustomItem implements InteractHooker {

    public static final String id = "eden_chime";

    public EdenChime() {
        super(id, "§bEdEn chime.mp3", ItemStack.of(Material.NETHER_BRICK), 16);
        List<Component> lore = List.of(
                Component.text("§5EdEn chime.mp3というファイルのみが入っている小さな音楽プレイヤー。"),
                Component.text("§5聞いているとなんか穏やかな気分になる。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5使用すると半径10m以内にいるプレイヤー全員が12秒間攻撃できなくなる。")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                item.setItemMeta(meta);
                this.baseItem = item;
            }
            return item;
        };
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        item.setAmount(item.getAmount() - 1);
        List<Player> players = CalculateAreaPlayers.getPlayersInArea(Game.getInstance(), null, player.getLocation(), 10);
        GameStatesManager manager = Game.getInstance().getGameStatesManager();

        //効果範囲に1回だけパーティクル出す
        player.getWorld().spawnParticle(Particle.NOTE, player.getLocation().add(0,1,0), 100, 5, 2, 5, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);

        for(Player targetPlayer : players){
            UUID targetUUID = targetPlayer.getUniqueId();

            if(manager.isExistEdenChimeTask(targetUUID)){
                BukkitRunnable oldTask = manager.getEdenChimeTasks().get(targetUUID);
                if(oldTask != null){
                    oldTask.cancel();
                }
                manager.setIsEnableAttack(targetUUID, true);
            }

            targetPlayer.sendMessage(Component.text("§cEdEn chimeが流れ始めた..."));
            manager.setIsEnableAttack(targetUUID, false);

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    targetPlayer.sendMessage(Component.text("§aEdEn chimeの効果が切れた！"));
                    manager.setIsEnableAttack(targetUUID, true);
                    manager.removeEdenChimeTask(targetUUID);
                }
            };
            task.runTaskLater(Dokkoi.getInstance(), 12 * 20);

            // タスクを登録
            manager.addEdenChimeTask(targetUUID, task);
        }
    }
}
