package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
import org.meyason.dokkoi.goal.Defender;
import org.meyason.dokkoi.goal.Police;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.goalitem.BuriBuriGuard;
import org.meyason.dokkoi.item.goalitem.KillerList;
import org.meyason.dokkoi.item.jobitem.Ketsumou;
import org.meyason.dokkoi.item.battleitems.HealingCrystal;
import org.meyason.dokkoi.item.goalitem.BuriBuriGuard;
import org.meyason.dokkoi.item.goalitem.KillerList;
import org.meyason.dokkoi.item.jobitem.Ketsumou;
import org.meyason.dokkoi.job.*;

import java.util.Objects;

public class InteractEvent implements Listener {

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

            if(event.getClickedBlock() instanceof Chest chest){
                if(game.getGameStatesManager().getPlayerJobs().get(player) instanceof Prayer prayer){
                    if(prayer.addLocationToAlreadyOpenedChests(chest.getLocation())){
                        player.sendMessage(Component.text("§b[ガチャポイント]§b このチェストは初めて開ける。ガチャポイントを1獲得しました。"));
                        prayer.addGachaCount(game, player);
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
                if (Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.SKILL)) {
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    Job job = manager.getPlayerJobs().get(player);
                    if (job.isSkillCoolDown(player)) {
                        player.sendActionBar(Component.text("§cスキルはクールダウン中です。"));
                        return;
                    }

                    job.playSoundEffectSkill(player);
                    // 執行者
                    if (job instanceof Executor) {
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        Vector velocity = direction.multiply(3.0);
                        Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                        manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));
                    }else if(job instanceof Lonely lonely){
                        lonely.skill();
                    } else if (job instanceof Bomber bomber) {
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        Vector velocity = direction.multiply(2.0);
                        Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                        manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));
                    }else if(job instanceof IronMaiden ironMaiden) {
                        ironMaiden.skill();
                    } else if (job instanceof Explorer explorer) {
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
                    }else if(job instanceof Prayer prayer) {
                        if(prayer.getGachaPoint() <= 0){
                            player.sendActionBar(Component.text("§cガチャポイントが足りません。"));
                            return;
                        }
                        prayer.skill();
                    }

                    job.setRemainCoolTimeSkill(job.getCoolTimeSkill());
                    job.chargeSkill(player, manager);

                } else if (Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.ULTIMATE_SKILL)) {
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    Job job = manager.getPlayerJobs().get(player);
                    if (job.isUltimateSkillCoolDown(player)) {
                        player.sendActionBar(Component.text("§cアルティメットはクールダウン中です。"));
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
                        manager.addProjectileData(projectile, new ProjectileData(player, projectile, customItem.getId()));
                    }else if(job instanceof IronMaiden ironMaiden){
                        ironMaiden.ultimate();
                    } else if (job instanceof Explorer explorer) {
                        explorer.ultimate();
                    } else if (job instanceof Prayer prayer) {
                        prayer.ultimate();
                    }

                    job.setRemainCoolTimeSkillUltimate(job.getCoolTimeSkillUltimate());
                    job.chargeUltimateSkill(player, manager);

                    // アイテム類
                } else if (Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.KILLER_LIST)) {
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    if (customItem instanceof KillerList killerList) {
                        killerList.skill(manager, player);
                    }
                } else if (Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.BURIBURIGUARD)) {
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    if(customItem instanceof BuriBuriGuard buriburiguard){
                        Defender defender = (Defender) game.getGameStatesManager().getPlayerGoals().get(player);
                        buriburiguard.skill(player, defender.getTargetPlayer());
                    }
                }else if(Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.TSUYOKUNARU)){
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK){
                        return;
                    }
                    event.setCancelled(true);
                    game.getGameStatesManager().addIsDeactivateDamageOnce(player, true);
                }else if (Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.HEARING_CRYSTAL)) {
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    if (customItem instanceof HealingCrystal) {
                        if (player.getHealth() == player.getMaxHealth()) {
                            player.sendMessage("§4既に最大体力です。");
                            return;
                        }
                        double newHealth = player.getHealth() + 5;
                        if (newHealth > player.getMaxHealth()) {
                            newHealth = player.getMaxHealth();
                        }
                        player.setHealth(newHealth);
                        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10, 1);
                        player.sendMessage("§a回復結晶を使用した！");

                        item.setAmount(item.getAmount() - 1);
                        player.getInventory().setItemInMainHand(item);
                    }
                }
            }
        }
    }
}
