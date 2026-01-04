package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.ClickEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.item.debug.Debug;
import org.meyason.dokkoi.item.goalitem.*;
import org.meyason.dokkoi.item.gunitem.ARMagazine;
import org.meyason.dokkoi.item.gunitem.HGMagazine;
import org.meyason.dokkoi.item.gunitem.SMGMagazine;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.item.jobitem.Ultimate;
import org.meyason.dokkoi.item.matching.JoinQueueItem;
import org.meyason.dokkoi.item.matching.QuitQueueItem;
import org.meyason.dokkoi.item.utilitem.FortuneBall;
import org.meyason.dokkoi.item.utilitem.IdiotDetector;
import org.meyason.dokkoi.menu.fortuneballmenu.FortuneBallMenu;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenu;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenuItem;

import java.util.Objects;

public class ItemInteractEvent{

    public static void onItemInteract(PlayerInteractEvent event, String itemID, String itemSerial, CustomItem customItem, ItemStack itemStack){
        Game game = Game.getInstance();
        Player player = event.getPlayer();

        if(game.getGameStatesManager().getGameState() == GameState.WAITING || game.getGameStatesManager().getGameState() == GameState.MATCHING){

            if(itemID.equals(JoinQueueItem.id)){
                event.setCancelled(true);
                Game.getInstance().addToMatchQueue(player);
            }else if(itemID.equals(QuitQueueItem.id)){
                event.setCancelled(true);
                Game.getInstance().removeFromMatchQueue(player);
            }


        }else if(game.getGameStatesManager().getGameState() == GameState.PREP){

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
            if(GameItem.getItem(itemID) instanceof InteractHooker hooker){
                if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                    return;
                }
                event.setCancelled(true);
                hooker.onInteract(event);
            }

            switch (itemID) {
                //FIXME: 誰か助けて死にます
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
                //FIXME: 誰か助けて死にます
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
                //FIXME: 誰か助けて死にます
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
                //FIXME: 誰か助けて死にます
                case TierPlayerList.id -> {
                    if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
                        return;
                    }
                    if (customItem instanceof TierPlayerList && itemSerial != null) {
                        CustomItem serialItem = game.getGameStatesManager().getCustomItemFromSerial(itemSerial);
                        if (!(serialItem instanceof TierPlayerList tierPlayerList)) {
                            return;
                        }
                        event.setCancelled(true);
                        tierPlayerList.skill(manager, player);
                    }
                }
            }
        }
    }
}
