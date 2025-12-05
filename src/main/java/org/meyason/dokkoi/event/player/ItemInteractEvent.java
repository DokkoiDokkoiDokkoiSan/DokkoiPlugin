package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.battleitem.HealingCrystal;
import org.meyason.dokkoi.item.battleitem.PotionBottleFull;
import org.meyason.dokkoi.item.dealeritem.Hayakunaru;
import org.meyason.dokkoi.item.dealeritem.Katakunaru;
import org.meyason.dokkoi.item.dealeritem.Kizukieru;
import org.meyason.dokkoi.item.dealeritem.Tsuyokunaru;
import org.meyason.dokkoi.item.debug.Debug;
import org.meyason.dokkoi.item.goalitem.BuriBuriGuard;
import org.meyason.dokkoi.item.goalitem.KillerList;
import org.meyason.dokkoi.item.goalitem.UnkillerList;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenu;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenuItem;

import java.util.Objects;

public class ItemInteractEvent implements Listener {

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event){
        Game game = Game.getInstance();
        Player player = event.getPlayer();
        if(game.getGameStatesManager().getGameState() == GameState.PREP){
            ItemStack item = event.getItem();
            if (item == null) {
                return;
            }
            NamespacedKey itemKey = new NamespacedKey(JavaPlugin.getPlugin(org.meyason.dokkoi.Dokkoi.class), GameItemKeyString.ITEM_NAME);
            ItemMeta meta = item.getItemMeta();

            if (meta == null) {
                return;
            }
            if (meta.getPersistentDataContainer().has(itemKey)) {
                String itemId = meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
                if (itemId == null) {
                    return;
                }
                if(itemId.equals(GoalSelectMenuItem.id)) {
                    if (game.getGameStatesManager().getPlayerJobs().get(player.getUniqueId()) != null) {
                        player.sendMessage("§c既に職業が選択されています。");
                        event.setCancelled(true);
                        return;
                    }
                    GoalSelectMenu goalSelectMenu = new GoalSelectMenu();
                    goalSelectMenu.sendMenu(event.getPlayer());
                    event.setCancelled(true);
                }
            }

        }else if(game.getGameStatesManager().getGameState() == GameState.IN_GAME) {
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



                if (isItem(container, itemKey, KillerList.id)) {
                    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    NamespacedKey serialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.UNIQUE_ITEM);
                    String itemSerial = container.get(serialKey, PersistentDataType.STRING);
                    if (customItem instanceof KillerList && itemSerial != null) {
                        CustomItem serialItem = game.getGameStatesManager().getCustomItemFromSerial(itemSerial);
                        if (!(serialItem instanceof KillerList killerList)) {
                            return;
                        }
                        killerList.skill(manager, player);
                    }

                }else if(isItem(container, itemKey, UnkillerList.id)){
                    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    GameStatesManager manager = game.getGameStatesManager();
                    NamespacedKey serialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.UNIQUE_ITEM);
                    String itemSerial = container.get(serialKey, PersistentDataType.STRING);
                    if (customItem instanceof UnkillerList && itemSerial != null) {
                        CustomItem serialItem = game.getGameStatesManager().getCustomItemFromSerial(itemSerial);
                        if (!(serialItem instanceof UnkillerList unkillerList)) {
                            return;
                        }
                        unkillerList.skill(manager, player);
                    }

                } else if (isItem(container, itemKey, BuriBuriGuard.id)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    if(customItem instanceof BuriBuriGuard){
                        NamespacedKey serialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.UNIQUE_ITEM);
                        String itemSerial = container.get(serialKey, PersistentDataType.STRING);
                        CustomItem serialItem = game.getGameStatesManager().getCustomItemFromSerial(itemSerial);
                        BuriBuriGuard buriburiguard = (BuriBuriGuard) serialItem;
                        buriburiguard.skill();
                    }


                }else if(isItem(container, itemKey, Tsuyokunaru.id)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    game.getGameStatesManager().addIsDeactivateDamageOnce(player.getUniqueId(), true);
                    Tsuyokunaru.activate(player, item);


                }else if(isItem(container, itemKey, Kizukieru.id)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Kizukieru.activate(player, item);


                }else if(isItem(container, itemKey, Hayakunaru.id)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Hayakunaru.activate(player, item);


                }else if(isItem(container, itemKey, Katakunaru.id)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Katakunaru.activate(player, item);


                } else if (isItem(container, itemKey, HealingCrystal.id)) {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    HealingCrystal.activate(player, item);


                }else if(isItem(container, itemKey, Debug.id)){
                    if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK){
                        return;
                    }
                    event.setCancelled(true);
                    if(event.getAction() == Action.LEFT_CLICK_BLOCK){
                        Location location = event.getClickedBlock().getLocation();
                        player.sendMessage(Component.text("§aクリックしたブロックの座標"));
                        player.sendMessage(Component.text(location.getX() + ", " + location.getY() + ", " + location.getZ()));
                    }
                }else if(isItem(container, itemKey, PotionBottleFull.id)){
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    PotionBottleFull.activate(player, item);
                }
            }
        }
    }

    private boolean isItem(PersistentDataContainer container, NamespacedKey itemKey, String gameItemKeyString){
        return Objects.equals(container.get(itemKey, PersistentDataType.STRING), gameItemKeyString);
    }
}
