package org.meyason.dokkoi.event.player;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.goal.Defender;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.battleitems.HealingCrystal;
import org.meyason.dokkoi.item.dealeritem.Hayakunaru;
import org.meyason.dokkoi.item.dealeritem.Katakunaru;
import org.meyason.dokkoi.item.dealeritem.Kizukieru;
import org.meyason.dokkoi.item.dealeritem.Tsuyokunaru;
import org.meyason.dokkoi.item.goalitem.BuriBuriGuard;
import org.meyason.dokkoi.item.goalitem.KillerList;

import java.util.Objects;

public class ItemInteractEvent implements Listener {

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if(game.getGameStatesManager().getGameState() == GameState.IN_GAME) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (!item.hasItemMeta()) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
            CustomItem customItem = CustomItem.getItem(item);
            if (customItem == null) {
                return;
            }
            if (container.has(itemKey, PersistentDataType.STRING)) {



                if (isItem(container, itemKey, GameItemKeyString.KILLERLIST)) {
                    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    if (customItem instanceof KillerList killerList) {
                        killerList.skill(manager, player);
                    }


                } else if (isItem(container, itemKey, GameItemKeyString.BURIBURIGUARD)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    if(customItem instanceof BuriBuriGuard buriburiguard){
                        Defender defender = (Defender) game.getGameStatesManager().getPlayerGoals().get(player);
                        buriburiguard.skill(player, defender.getTargetPlayer());
                    }


                }else if(isItem(container, itemKey, GameItemKeyString.TSUYOKUNARU)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    game.getGameStatesManager().addIsDeactivateDamageOnce(player, true);
                    Tsuyokunaru.activate(player, item);


                }else if(isItem(container, itemKey, GameItemKeyString.KIZUKIERU)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Kizukieru.activate(player, item);


                }else if(isItem(container, itemKey, GameItemKeyString.HAYAKUNARU)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Hayakunaru.activate(player, item);


                }else if(isItem(container, itemKey, GameItemKeyString.KATAKUNARU)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Katakunaru.activate(player, item);


                } else if (isItem(container, itemKey, GameItemKeyString.HEALINGCRYSTAL)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    if (customItem instanceof HealingCrystal) {
                        if (player.getHealth() == player.getMaxHealth()) {
                            player.sendActionBar("§c既に最大体力です。");
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

    private boolean isItem(PersistentDataContainer container, NamespacedKey itemKey, String gameItemKeyString){
        return Objects.equals(container.get(itemKey, PersistentDataType.STRING), gameItemKeyString);
    }
}
