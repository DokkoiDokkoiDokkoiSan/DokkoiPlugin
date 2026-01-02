package org.meyason.dokkoi.event.player;

import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.DokkoiDatabaseAPI;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameLocation;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.game.LPManager;
import org.meyason.dokkoi.goal.GangStar;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.goal.Police;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.battleitem.RedHelmet;
import org.meyason.dokkoi.job.Bomber;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.job.Prayer;
import org.meyason.dokkoi.job.Summoner;
import org.meyason.dokkoi.job.context.PassiveContext;
import org.meyason.dokkoi.network.PacketProcess;
import org.meyason.dokkoi.network.PacketSender;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public class DeathEvent {

    public static void kill(Player killer, Player dead){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        LPManager lpManager = Game.getInstance().getLPManager();
        UUID killerUUID = killer.getUniqueId();
        UUID deadUUID = dead.getUniqueId();

        // 生き返らせるならこの辺

        if(manager.getPlayerJobs().get(deadUUID) instanceof Bomber bomber){
            bomber.passive(PassiveContext.create());
            if(bomber.isPassiveActive()) return;
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
            World world = dead.getWorld();
            Location location = GameLocation.getInstance().respawnLocations.get(new Random().nextInt(GameLocation.getInstance().respawnLocations.size())).toLocation(world);
            dead.teleport(location);
            dead.setHealth(dead.getMaxHealth());
            return;
        }

        // 以下死亡処理

        manager.removeAlivePlayer(dead.getUniqueId());
        if(killer != null)  manager.getKillerList().put(killerUUID, deadUUID);
        manager.removeAttackedPlayer(deadUUID);
        manager.removeDamagedPlayer(deadUUID);

        dead.sendMessage("§cあなたは§4§l死亡§r§cしました");
        if(killer != null) {
            dead.sendMessage("§eキルしたプレイヤー: §l§c" + killer.getName() + "§r§e");
            killer.sendMessage("§aあなたは§l§6" + dead.getName() + "§r§aを倒しました");
        }

        boolean hasRedHelmet = false;
        ItemStack helmet = dead.getInventory().getHelmet();
        if(helmet != null){
            ItemMeta meta = helmet.getItemMeta();
            if(meta != null){
                PersistentDataContainer container = meta.getPersistentDataContainer();
                NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
                if(container.has(itemKey, PersistentDataType.STRING)){
                    String gameItemName = container.get(itemKey, PersistentDataType.STRING);
                    if(gameItemName != null) {
                        if (gameItemName.equals(RedHelmet.id)) {
                            hasRedHelmet = true;
                            if (killer != null) lpManager.addLP(killer.getUniqueId(), 30L);
                        }
                    }
                }
            }
        }

        if(!hasRedHelmet) {
            if (killer != null && !manager.isNaito(deadUUID) && !manager.getPlayerGoals().get(killerUUID).isKillable(dead)) {
                String borderColor = "§6";
                String horizontal = "─".repeat(32);
                List<String> boxMessage = List.of(
                        "§c§lペナルティ§r§c：許可されていない殺害",
                        "§6" + dead.getName(),
                        "§cは、殺害できるプレイヤーではない。赤い帽子を被せられた。"
                );
                killer.sendMessage(borderColor + "┌" + horizontal + "┐");
                for (String box : boxMessage) {
                    String line = box;
                    Function<String, Integer> visibleLen =
                            s -> s.replaceAll("(?i)§.", "").length();
                    int length = visibleLen.apply(box);
                    int paddingLength = (horizontal.length() + 1 - length) * 2;
                    String forwardPadding = " ".repeat(paddingLength / 2);
                    String backPadding = " ".repeat(paddingLength - paddingLength / 2);
                    killer.sendMessage(Component.text(forwardPadding + line + backPadding));
                }
                killer.sendMessage(borderColor + "└" + horizontal + "┘");
                try {
                    RedHelmet item = (RedHelmet) GameItem.getItem(RedHelmet.id);
                    item.setPlayerHead(killer);
                } catch (NoGameItemException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }else {
            if(killer!=null) killer.sendMessage("§e[ペナルティ] §a" + dead.getName() + " §aは殺害できないプレイヤーですが，赤い帽子を被っていたためペナルティは免れました。");
        }

        if(manager.isEnableKillerList()){
            HashMap<UUID, Goal> playerGoals = manager.getPlayerGoals();
            for(UUID uuid : manager.getAlivePlayers()){
                Player player = Bukkit.getPlayer(uuid);
                if(player == null){continue;}
                Goal goal = playerGoals.get(uuid);
                if(goal instanceof Police police){
                    if(killer != null && !player.equals(killer)) {
                        player.sendMessage("§a[殺すノート] §c" + killer.getName() + "§a が " + dead.getName() + " §aを倒しました");
                    }
                    police.getKillerList().updateKillerList();
                }else if(goal instanceof GangStar gangStar){
                    gangStar.getUnKillerList().updateUnKillerList();
                }
            }
        }

        dead.setGameMode(GameMode.SPECTATOR);
        dead.setHealth(dead.getMaxHealth());

        Game.getInstance().getGameStatesManager().getJoinedPlayers().forEach(p -> {
            Player joinedPlayer = Bukkit.getPlayer(p);
            if(joinedPlayer != null && joinedPlayer.isOnline()){
                PacketContainer pk = PacketProcess.showNameTag(joinedPlayer, null);
                PacketSender.sendPacket(dead, pk);
            }
        });

        lpManager.addLP(killerUUID, 10L);

        if(manager.getExistSummoner()){
            for(Job job : manager.getPlayerJobs().values()){
                if(job instanceof Summoner summoner){
                    summoner.passive(dead);
                }
            }
        }

        World world = dead.getWorld();
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        for(ItemStack item : dead.getInventory().getContents()){
            if(item == null) continue;
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if(container.has(itemKey)) {
                    String gameItemName = container.get(itemKey, PersistentDataType.STRING);
                    if (GameItem.isCustomItem(item)) {
                        try {
                            CustomItem customItem = GameItem.getItem(gameItemName);
                            if (customItem.isUnique) {
                                item.setAmount(0);
                            }
                        } catch (NoGameItemException e) {
                            continue;
                        }
                    }
                }
            }

            world.dropItemNaturally(dead.getLocation(), item).setPickupDelay(10);
        }
    }
}
