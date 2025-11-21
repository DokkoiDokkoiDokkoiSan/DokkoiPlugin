package org.meyason.dokkoi.event.player;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

import java.util.List;

public class AttackEvent implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        Entity attacker = event.getDamager();
        Entity damaged = event.getEntity();
        if(!(attacker instanceof Player killer) || !(damaged instanceof Player dead)) return;

        if(dead.getNoDamageTicks() >= 10){
            event.setCancelled(true);
            return;
        }
        if(Game.getInstance().getGameState() != GameState.IN_GAME) {
            event.setCancelled(true);
            return;
        }

        double afterHealth = dead.getHealth() - event.getFinalDamage();
        // 死亡処理
        if(afterHealth < 0) {
            event.setCancelled(true);

            List<Player> alivePlayers = Game.getInstance().getAlivePlayers();
            alivePlayers.removeIf(p -> p.getUniqueId().equals(dead.getUniqueId()));
            Game.getInstance().setAlivePlayers(alivePlayers);
            Game.getInstance().getKillerList().put(killer, dead);

            dead.sendMessage("§cあなたは§l§4死亡§r§cしました");
            killer.sendMessage("§aあなたは§l§4" + dead.getName() + "§r§aを倒しました");

            dead.setGameMode(GameMode.SPECTATOR);
            dead.setHealth(20.0);

            World world = dead.getWorld();
            for(ItemStack item : dead.getInventory().getContents()){
                if(item == null) continue;
                if(item.getItemMeta() != null){
                    if(GameItem.isCustomItem(item)){
                        CustomItem customItem = (CustomItem)item.getItemMeta();
                        if(customItem.isUnique){
                            continue;
                        }
                    }
                }

                world.dropItemNaturally(dead.getLocation(), item).setPickupDelay(10);
            }
        }
    }
}
