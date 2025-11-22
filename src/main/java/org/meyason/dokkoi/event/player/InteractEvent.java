package org.meyason.dokkoi.event.player;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
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
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.gacha.GachaMachine;
import org.meyason.dokkoi.item.gacha.menu.GachaPointMenu;
import org.meyason.dokkoi.job.Executor;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.scheduler.SkillScheduler;

import java.util.*;

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

            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (!item.hasItemMeta()) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
            if (container.has(itemKey, PersistentDataType.STRING)) {

                if (Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.GACHA_MACHINE)) {
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if (customItem.isUnique && customItem.getId().equals(GachaMachine.id)) {
                        event.setCancelled(true);
                        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            ItemStack newItem = GachaMachine.doGacha(event.getPlayer());
                            event.getPlayer().getInventory().addItem(Objects.requireNonNull(newItem));
                        } else {
                            GachaPointMenu menu = new GachaPointMenu();
                            menu.sendMenu(event.getPlayer());
                        }
                    }
                }else if(Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.SKILL)){
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK){
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    Job job = manager.getPlayerJobs().get(player);
                    if(SkillScheduler.isSkillCoolDown(player)){
                        player.sendMessage("§cスキルはクールダウン中です。");
                        return;
                    }
                    if(job instanceof Executor){
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        Vector velocity = direction.multiply(1.0);
                        Snowball projectile = player.launchProjectile(Snowball.class, velocity);
                        manager.addProjectileData(projectile, new ProjectileData(player));
                        SkillScheduler.chargeSkill(player, manager);
                    }

                }else if(Objects.equals(container.get(itemKey, PersistentDataType.STRING), GameItemKeyString.ULTIMATE_SKILL)){
                    CustomItem customItem = CustomItem.getItem(item);
                    if (customItem == null) {
                        return;
                    }
                    if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK){
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    Job job = manager.getPlayerJobs().get(player);
                    if(SkillScheduler.isUltimateSkillCoolDown(player)){
                        player.sendMessage("§cアルティメットスキルはクールダウン中です。");
                        return;
                    }
                    if(job instanceof Executor executor){
                        executor.ultimate();
                        SkillScheduler.chargeUltimateSkill(player, manager);
                    }
                }


            }

        }


    }
}
