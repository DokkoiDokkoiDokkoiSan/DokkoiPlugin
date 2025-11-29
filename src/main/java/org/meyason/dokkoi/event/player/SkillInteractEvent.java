package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.ProjectileData;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.jobitem.*;
import org.meyason.dokkoi.job.*;

import java.util.Objects;

public class SkillInteractEvent implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if(game.getGameStatesManager().getGameState() == GameState.WAITING || game.getGameStatesManager().getGameState() == GameState.END) return;

        if(game.getGameStatesManager().getGameState() == GameState.PREP && event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            if(block instanceof Container){
                event.setCancelled(true);
            }

        }else if(game.getGameStatesManager().getGameState() == GameState.IN_GAME) {

            if(event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof InventoryHolder chest) {
                if(game.getGameStatesManager().getPlayerJobs().get(player.getUniqueId()) instanceof Prayer prayer){
                    if(prayer.addLocationToAlreadyOpenedChests(chest.getInventory().getLocation())){
                        player.sendActionBar(Component.text("§b[ガチャポイント]§b このチェストは初めて開ける。"));
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0F, 1.0F);
                        prayer.addGachaPoint(1);
                    }
                }
            }

            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (!item.hasItemMeta()) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
            if (container.has(itemKey, PersistentDataType.STRING)) {

                // スキル発動
                if (Objects.equals(container.get(itemKey, PersistentDataType.STRING), Skill.id)) {
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    Job job = manager.getPlayerJobs().get(player.getUniqueId());
                    if (job.isSkillCoolDown(player)) {
                        player.sendActionBar(Component.text("§cスキルはクールダウン中です。"));
                        return;
                    }

                    // 執行者
                    switch (job) {
                        case Executor executor -> {
                            Vector direction = player.getEyeLocation().getDirection().normalize();
                            Vector velocity = direction.multiply(3.0);
                            Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                            manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));
                        }
                        case Lonely lonely -> lonely.skill();
                        case Bomber bomber -> {
                            Vector direction = player.getEyeLocation().getDirection().normalize();
                            Vector velocity = direction.multiply(2.0);
                            Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                            manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));
                        }
                        case IronMaiden ironMaiden -> ironMaiden.skill();
                        case Explorer explorer -> {
                            if (explorer.getHaveKetsumouCount() <= 0) {
                                player.sendActionBar(Component.text("§c投擲できる§9§lけつ毛§r§cがない。"));
                                return;
                            }
                            Vector direction = player.getEyeLocation().getDirection().normalize();
                            Vector velocity = direction.multiply(2.0);
                            Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                            ItemStack itemStack = new ItemStack(Material.PALE_HANGING_MOSS);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            if (itemMeta != null) {
                                itemStack.setItemMeta(itemMeta);
                            }
                            projectile.setItem(itemStack);

                            for (ItemStack iS : player.getInventory().getContents()) {
                                if (iS == null) continue;
                                if (iS.getItemMeta() != null) {
                                    CustomItem cI = CustomItem.getItem(iS);
                                    if (cI instanceof Ketsumou) {
                                        player.getInventory().removeItem(iS);
                                        break;
                                    }
                                }
                            }
                            manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));
                        }
                        case Prayer prayer -> {
                            if (prayer.getGachaPoint() <= 0) {
                                player.sendActionBar(Component.text("§cガチャポイントが足りません。"));
                                return;
                            }
                            prayer.skill();
                        }
                        default -> {
                        }
                    }
                    job.playSoundEffectSkill(player);

                    job.setRemainCoolTimeSkill(job.getCoolTimeSkill());
                    job.chargeSkill(player, manager);

                } else if (Objects.equals(container.get(itemKey, PersistentDataType.STRING), Ultimate.id)) {
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    Job job = manager.getPlayerJobs().get(player.getUniqueId());
                    if (job.isUltimateSkillCoolDown(player)) {
                        player.sendActionBar(Component.text("§cアルティメットはクールダウン中です。"));
                        return;
                    }
                    job.playSoundEffectUltimateSkill(player);
                    switch (job) {
                        case Executor executor -> executor.ultimate();
                        case Lonely lonely -> lonely.ultimate();
                        case Bomber bomber -> {
                            Vector direction = player.getEyeLocation().getDirection().normalize();
                            Vector velocity = direction.multiply(2.0);
                            Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                            manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));
                        }
                        case IronMaiden ironMaiden -> ironMaiden.ultimate();
                        case Explorer explorer -> explorer.ultimate();
                        case Prayer prayer -> prayer.ultimate();
                        default -> {
                        }
                    }

                    job.setRemainCoolTimeSkillUltimate(job.getCoolTimeSkillUltimate());
                    job.chargeUltimateSkill(player, manager);

                }
            }
        }
    }
}
