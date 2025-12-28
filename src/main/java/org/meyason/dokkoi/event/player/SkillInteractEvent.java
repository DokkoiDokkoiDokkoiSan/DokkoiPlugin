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
import org.bukkit.event.EventPriority;
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
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.item.jobitem.*;
import org.meyason.dokkoi.job.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SkillInteractEvent{

    public static void onSkillInteract(PlayerInteractEvent event, String itemID){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(game.getGameStatesManager().getGameState() == GameState.IN_GAME) {

            event.setCancelled(true);
            GameStatesManager manager = game.getGameStatesManager();
            Job job = manager.getPlayerJobs().get(player.getUniqueId());

            // スキル発動
            if (itemID.equals(Skill.id)) {
                if (job.isSkillCoolDown(player)) {
                    player.sendActionBar(Component.text("§cスキルはクールダウン中です。"));
                    return;
                }

                // 執行者
                if (job instanceof Executor executor) {
                    Vector direction = player.getEyeLocation().getDirection().normalize();
                    Vector velocity = direction.multiply(3.0);
                    Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                    manager.addProjectileData(projectile, new ProjectileData(player, projectile, Skill.id));

                } else if (job instanceof Lonely lonely) {
                    lonely.skill();

                } else if (job instanceof Bomber) {
                    Vector direction = player.getEyeLocation().getDirection().normalize();
                    Vector velocity = direction.multiply(2.0);
                    Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                    manager.addProjectileData(projectile, new ProjectileData(player, projectile, Skill.id));

                } else if (job instanceof IronMaiden ironMaiden) {
                    ironMaiden.skill();

                } else if (job instanceof Explorer explorer) {
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
                    manager.addProjectileData(projectile, new ProjectileData(player, projectile, Skill.id));

                } else if (job instanceof Prayer prayer) {
                    if (prayer.getGachaPoint() <= 0) {
                        player.sendActionBar(Component.text("§cガチャポイントが足りません。"));
                        return;
                    }
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendActionBar(Component.text("§cインベントリに空きがありません。"));
                        return;
                    }
                    prayer.skill();

                } else if (job instanceof DrugStore drugStore) {
                    drugStore.skill();
                } else if (job instanceof Photographer photographer) {
                    photographer.skill();
                } else if (job instanceof Summoner summoner) {
                    summoner.skill();
                } else if (job instanceof Sniper sniper){
                        sniper.skill();
                }
                job.playSoundEffectSkill(player);

                job.setRemainCoolTimeSkill(job.getCoolTimeSkill());
                job.chargeSkill(player, manager);


                // アルティメット発動
            } else {
                if (job.isUltimateSkillCoolDown(player)) {
                    player.sendActionBar(Component.text("§cアルティメットはクールダウン中です。"));
                    return;
                } else if (job.getRemainCoolTimeSkill() == -1) {
                    player.sendActionBar(Component.text("§cアルティメットスキルは既に使用しています。"));
                    return;
                }
                job.playSoundEffectUltimateSkill(player);
                if (job instanceof Executor executor) {
                    executor.ultimate();

                } else if (job instanceof Lonely lonely) {
                    lonely.ultimate();

                } else if (job instanceof Bomber bomber) {
                    Vector direction = player.getEyeLocation().getDirection().normalize();
                    Vector velocity = direction.multiply(2.0);
                    Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                    manager.addProjectileData(projectile, new ProjectileData(player, projectile, Ultimate.id));

                } else if (job instanceof IronMaiden ironMaiden) {
                    ironMaiden.ultimate();

                } else if (job instanceof Explorer explorer) {
                    explorer.ultimate();

                } else if (job instanceof Prayer prayer) {
                    prayer.ultimate();

                } else if (job instanceof DrugStore drugStore) {
                    NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
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
                } else if (job instanceof Photographer photographer) {
                    if(photographer.canUseUltimate()){
                        photographer.ultimate();
                    }else{
                        player.sendActionBar(Component.text("§cアルティメットを使用するには一人以上を撮影する必要があります。"));
                        return;
                    }

                } else if (job instanceof Summoner summoner) {
                    List<UUID> targetPlayers = manager.getVictims();
                    if (targetPlayers.isEmpty()) {
                        player.sendActionBar(Component.text("§c召喚できる対象がいない。"));
                        return;
                    }
                    summoner.ultimate(targetPlayers);
                } else if (job instanceof Sniper sniper){
                    sniper.ultimate();
                }

                //FIXME: 仮で1000にしてるけど要調整
                job.setRemainCoolTimeSkillUltimate(1000);
                job.chargeUltimateSkill(player, manager);

            }
        }
    }
}
