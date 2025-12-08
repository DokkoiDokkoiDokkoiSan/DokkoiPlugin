package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.ClickEvent;
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
import org.meyason.dokkoi.exception.NoGameItemException;
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
import org.meyason.dokkoi.item.gunitem.HGMagazine;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.item.jobitem.Ultimate;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenu;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenuItem;

import java.util.Objects;

public class ItemInteractEvent{

    public static void onItemInteract(PlayerInteractEvent event, String itemID, String itemSerial, CustomItem customItem){
        Game game = Game.getInstance();
        Player player = event.getPlayer();

        if(game.getGameStatesManager().getGameState() == GameState.PREP){

            if(itemID.equals(GoalSelectMenuItem.id)) {
                if (game.getGameStatesManager().getPlayerGoals().get(player.getUniqueId()) != null) {
                    player.sendMessage("§c既に勝利条件が選択されています。");
                    event.setCancelled(true);
                    return;
                }
                GoalSelectMenu goalSelectMenu = new GoalSelectMenu();
                goalSelectMenu.sendMenu(event.getPlayer());
                event.setCancelled(true);
            }

        }else if(game.getGameStatesManager().getGameState() == GameState.IN_GAME) {
            GameStatesManager manager = game.getGameStatesManager();


            switch (itemID) {
                case KillerList.id -> {
                    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    if (customItem instanceof KillerList && itemSerial != null) {
                        CustomItem serialItem = game.getGameStatesManager().getCustomItemFromSerial(itemSerial);
                        if (!(serialItem instanceof KillerList killerList)) {
                            return;
                        }
                        event.setCancelled(true);
                        killerList.skill(manager, player);
                    }

                }
                case UnkillerList.id -> {
                    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    if (customItem instanceof UnkillerList && itemSerial != null) {
                        CustomItem serialItem = game.getGameStatesManager().getCustomItemFromSerial(itemSerial);
                        if (!(serialItem instanceof UnkillerList unkillerList)) {
                            return;
                        }
                        event.setCancelled(true);
                        unkillerList.skill(manager, player);
                    }

                }
                case BuriBuriGuard.id -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    if (customItem instanceof BuriBuriGuard) {
                        event.setCancelled(true);
                        CustomItem serialItem = game.getGameStatesManager().getCustomItemFromSerial(itemSerial);
                        BuriBuriGuard buriburiguard = (BuriBuriGuard) serialItem;
                        buriburiguard.skill();
                    }
                }
                case Tsuyokunaru.id -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    game.getGameStatesManager().addIsDeactivateDamageOnce(player.getUniqueId(), true);
                    Tsuyokunaru.activate(player, customItem.getItem());
                }
                case Kizukieru.id -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Kizukieru.activate(player, customItem.getItem());
                }
                case Hayakunaru.id -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Hayakunaru.activate(player, customItem.getItem());
                }
                case Katakunaru.id -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    Katakunaru.activate(player, customItem.getItem());
                }
                case HealingCrystal.id -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    HealingCrystal.activate(player, customItem.getItem());
                }
                case Debug.id -> {
                    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        Location location = Objects.requireNonNull(event.getClickedBlock()).getLocation();
                        player.sendMessage(Component.text("§aクリックしたブロックの座標"));
                        player.sendMessage(Component.text(location.getX() + ", " + location.getY() + ", " + location.getZ()));
                    }
                }
                case PotionBottleFull.id -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    PotionBottleFull.activate(player, customItem.getItem());
                }
                case HGMagazine.id -> {
                    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                        return;
                    }
                    event.setCancelled(true);
                    HGMagazine.activate(player);
                }
            }
        }
    }
}
