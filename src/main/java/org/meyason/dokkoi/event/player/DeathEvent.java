package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.goal.Police;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.battleitem.RedHelmet;
import org.meyason.dokkoi.job.Bomber;
import org.meyason.dokkoi.job.Prayer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class DeathEvent {

    public static void kill(Player killer, Player dead){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        UUID killerUUID = killer.getUniqueId();
        UUID deadUUID = dead.getUniqueId();

        if(manager.getPlayerJobs().get(deadUUID) instanceof Bomber bomber){
            if(bomber.passive()){
                return;
            }
        }

        if(manager.getPlayerJobs().get(deadUUID) instanceof Prayer prayer){
            if(prayer.getHasStrongestStrongestBall()){
                dead.sendActionBar(Component.text("§aもっと最強のたまたま§bが攻撃を許さない！"));
                dead.setHealth(dead.getMaxHealth());
                return;
            }
        }

        if(manager.getPlayerGoals().get(deadUUID).tier == Tier.TIER_3 &&
                !manager.getPlayerGoals().get(deadUUID).isRevived){
            manager.getPlayerGoals().get(deadUUID).isRevived = true;
            dead.sendMessage("§aあなたはティア3勝利条件なので，§l§4復活§r§aしました");
            // いったん2mうしろにテレポート TODO: マップ内にランダムテレポート
            dead.teleport(dead.getLocation().subtract(dead.getLocation().getDirection().setY(0).normalize().multiply(1)));
            dead.setHealth(dead.getMaxHealth());
            return;
        }

        manager.removeAlivePlayer(dead.getUniqueId());
        manager.getKillerList().put(killerUUID, deadUUID);
        manager.removeAttackedPlayer(deadUUID);
        manager.removeDamagedPlayer(deadUUID);

        dead.sendMessage("§cあなたは§4§l死亡§r§cしました");
        dead.sendMessage("§eキルしたプレイヤー: §l§c" + killer.getName() + "§r§e");
        killer.sendMessage("§aあなたは§l§6" + dead.getName() + "§r§aを倒しました");

        if(!manager.getPlayerGoals().get(killerUUID).isKillable(dead)){
            String borderColor = "§6";
            String horizontal = "─".repeat(32);
            List<String> boxMessage = List.of(
                    "§c§lペナルティ§r§c：許可されていない殺害",
                    "§6" + dead.getName(),
                    "§cは、殺害できるプレイヤーではない。赤い帽子を被せられた。"
            );
            killer.sendMessage(borderColor + "┌" + horizontal + "┐");
            for(String box : boxMessage){
                String line = box;
                Function<String, Integer> visibleLen =
                        s -> s.replaceAll("(?i)§.", "").length();
                int length = visibleLen.apply(box);
                int paddingLength = (horizontal.length() + 1 - length) * 2;
                String forwardPadding = " ".repeat(paddingLength / 2);
                String backPadding = " ".repeat(paddingLength - paddingLength / 2);
                dead.sendMessage(forwardPadding.length() + " " + backPadding.length());
                killer.sendMessage(Component.text(forwardPadding + line + backPadding));
            }
            killer.sendMessage(borderColor + "└" + horizontal + "┘");
            RedHelmet item = (RedHelmet) GameItem.getItem(RedHelmet.id);
            if(item == null){
                killer.sendMessage(Component.text("§4エラーが発生しました．管理者に連絡してください：赤い帽子取得失敗"));
                return;
            }
            item.setPlayerHead(killer);
        }

        if(manager.isEnableKillerList()){
            HashMap<UUID, Goal> playerGoals = manager.getPlayerGoals();
            for(UUID uuid : manager.getAlivePlayers()){
                Player player = Bukkit.getPlayer(uuid);
                if(player == null){continue;}
                Goal goal = playerGoals.get(uuid);
                if(goal instanceof Police police){
                    if(player.equals(killer)){continue;}
                    player.sendMessage("§a[殺すノート] §c" + killer.getName() + "§a が " + dead.getName() + " §aを倒しました");
                    police.killerList.updateKillerList();
                }
            }
        }

        dead.setGameMode(GameMode.SPECTATOR);
        dead.setHealth(dead.getMaxHealth());

        World world = dead.getWorld();
        List<String> gameItemList = new java.util.ArrayList<>(List.copyOf(GameItemKeyString.getGameItemKeyStringHashMap()));
        gameItemList.remove(GameItemKeyString.ITEM_NAME);
        for(ItemStack item : dead.getInventory().getContents()){
            if(item == null) continue;
            if(item.getItemMeta() != null){
                if(GameItem.isCustomItem(item)){
                    for(String gameItemName : gameItemList){
                        CustomItem customItem = GameItem.getItem(gameItemName);
                        if(customItem != null && customItem.isUnique){
                            item.setAmount(0);
                            break;
                        }
                    }
                }
            }

            world.dropItemNaturally(dead.getLocation(), item).setPickupDelay(10);
        }
    }
}
