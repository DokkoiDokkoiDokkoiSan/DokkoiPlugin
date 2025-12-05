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
import org.bukkit.inventory.PlayerInventory;
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
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.item.jobitem.*;
import org.meyason.dokkoi.job.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
                if(game.getGameStatesManager().isNaito(player.getUniqueId())){
                    event.setCancelled(true);
                    return;
                }
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
                    if(job instanceof Executor executor) {
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        Vector velocity = direction.multiply(3.0);
                        Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                        manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));

                    } else if(job instanceof Lonely lonely) {
                        lonely.skill();

                    } else if(job instanceof Bomber) {
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        Vector velocity = direction.multiply(2.0);
                        Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                        manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));

                    }else if(job instanceof IronMaiden ironMaiden) {
                        ironMaiden.skill();

                    }else if(job instanceof Explorer explorer) {
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

                    } else if(job instanceof Prayer prayer) {
                        if (prayer.getGachaPoint() <= 0) {
                            player.sendActionBar(Component.text("§cガチャポイントが足りません。"));
                            return;
                        }
                        if(player.getInventory().firstEmpty() == -1) {
                            player.sendActionBar(Component.text("§cインベントリに空きがありません。"));
                            return;
                        }
                        prayer.skill();

                    }else if(job instanceof DrugStore drugStore) {
                        drugStore.skill();
                    }else if(job instanceof Summoner summoner) {
                        summoner.skill();
                    }
                    job.playSoundEffectSkill(player);

                    job.setRemainCoolTimeSkill(job.getCoolTimeSkill());
                    job.chargeSkill(player, manager);


                    // アルティメット発動
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
                    if(job instanceof Executor executor) {
                        executor.ultimate();

                    }else if(job instanceof Lonely lonely) {
                        lonely.ultimate();

                    }else if(job instanceof Bomber bomber) {
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        Vector velocity = direction.multiply(2.0);
                        Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                        manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));

                    }else if(job instanceof IronMaiden ironMaiden) {
                        ironMaiden.ultimate();

                    }else if(job instanceof Explorer explorer) {
                        explorer.ultimate();

                    }else if(job instanceof Prayer prayer) {
                        prayer.ultimate();

                    }else if(job instanceof DrugStore drugStore) {
                        List<String> drugList = new ArrayList<>();
                        PlayerInventory inventory = player.getInventory();
                        for (ItemStack i : inventory.getContents()) {
                            if (i == null) continue;
                            ItemMeta m = i.getItemMeta();
                            if (m == null) continue;
                            if (m.getPersistentDataContainer().has(itemKey)) {
                                CustomItem c = CustomItem.getItem(i);
                                if (c instanceof Katakunaru) {
                                    drugList.add(Katakunaru.id);
                                } else if (c instanceof Kizukieru) {
                                    drugList.add(Kizukieru.id);
                                } else if (c instanceof Hayakunaru) {
                                    drugList.add(Hayakunaru.id);
                                } else if (c instanceof Tsuyokunaru) {
                                    drugList.add(Tsuyokunaru.id);
                                } else if (c instanceof Korehamaru) {
                                    drugList.add(Korehamaru.id);
                                }
                            }
                        }
                        if (drugList.isEmpty()) {
                            player.sendActionBar(Component.text("§c強化できる薬を所持していない。"));
                            return;
                        }
                        drugStore.ultimate(drugList);

                    }else if(job instanceof Summoner summoner) {
                        List<UUID> targetPlayers = manager.getVictims();
                        if(targetPlayers.isEmpty()){
                            player.sendActionBar(Component.text("§c召喚できる対象がいない。"));
                            return;
                        }
                        summoner.ultimate(targetPlayers);
                    }

                    job.setRemainCoolTimeSkillUltimate(job.getCoolTimeSkillUltimate());
                    job.chargeUltimateSkill(player, manager);

                }
            }
        }
    }
}
