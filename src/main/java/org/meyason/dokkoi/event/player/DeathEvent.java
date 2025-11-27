package org.meyason.dokkoi.event.player;

import net.kyori.adventure.text.Component;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.goal.Police;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.job.Bomber;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import java.util.HashMap;
import java.util.List;

public class DeathEvent {

    public static void kill(Player killer, Player dead){
        GameStatesManager manager = Game.getInstance().getGameStatesManager();

        if(manager.getPlayerJobs().get(dead) instanceof Bomber bomber){
            if(bomber.passive()){
                return;
            }
        }

        if(manager.getPlayerGoals().get(dead).tier == Tier.TIER_3 &&
                !manager.getPlayerGoals().get(dead).isRevived){
            manager.getPlayerGoals().get(dead).isRevived = true;
            dead.sendMessage(Component.text("§aあなたはティア3勝利条件なので，§l§4復活§r§aしました"));
            // いったん2mうしろにテレポート TODO: マップ内にランダムテレポート
            Vector3d pos = dead.location().position();
            Vector3d rot = dead.rotation();
            double yaw = Math.toRadians(rot.y());
            double dx = -2 * Math.sin(yaw);
            double dz = 2 * Math.cos(yaw);
            double length = Math.sqrt(dx * dx + dz * dz);
            if(length != 0){
                dx /= length;
                dz /= length;
            }
            ServerLocation newLocation = dead.serverLocation().add(dx, 0, dz);

            dead.setLocation(newLocation);
            dead.offer(Keys.HEALTH, 40.0);
            return;
        }

        manager.removeAlivePlayer(dead);
        manager.getKillerList().put(killer, dead);
        manager.removeAttackedPlayer(dead);
        manager.removeDamagedPlayer(dead);

        dead.sendMessage(Component.text("§cあなたは§l§4死亡§r§cしました"));
        dead.sendMessage(Component.text("§eキルしたプレイヤー: §l§c" + killer.name() + "§r§e"));
        killer.sendMessage(Component.text("§aあなたは§l§6" + dead.name() + "§r§aを倒しました"));

        if(manager.isEnableKillerList()){
            HashMap<Player, Goal> playerGoals = manager.getPlayerGoals();
            for(Player p : manager.getAlivePlayers()){
                Goal goal = playerGoals.get(p);
                if(goal instanceof Police police){
                    if(p.equals(killer)){continue;}
                    p.sendMessage(Component.text("§a[殺すノート] §c" + killer.name() + "§a が " + dead.name() + " §aを倒しました"));
                    police.killerList.updateKillerList();
                }
            }
        }

        dead.offer(Keys.GAME_MODE, GameModes.SPECTATOR.get());
        dead.offer(Keys.HEALTH, 40.0);

        ServerPlayer serverDead = (ServerPlayer) dead;
        ServerWorld world = serverDead.world();
        Inventory inventory = serverDead.inventory();

        List<String> gameItemList = GameItemKeyString.getGameItemKeyStringHashMap();

        for(Slot slot : inventory.slots()){
            ItemStack stack = slot.peek().getOrElse(null, ItemStack.empty());
            if(stack == null || stack.isEmpty()) {
                continue;
            }

            if(GameItem.isCustomItem(stack)){
                for(String gameItemName : gameItemList){
                    CustomItem customItem = GameItem.getItem(gameItemName);
                    if(customItem != null && customItem.isUnique){
                        slot.set(ItemStack.empty());
                        break;
                    }
                }
            }

            ServerLocation dropLoc = serverDead.serverLocation();
            Item itemEntity = (Item) world.createEntity(EntityTypes.ITEM, dropLoc.position());
            itemEntity.offer(Keys.ITEM_STACK_SNAPSHOT, stack.asImmutable());
            itemEntity.offer(Keys.PICKUP_DELAY, Ticks.of(10));

            world.spawnEntity(itemEntity);

            // インベントリから削除
            slot.set(ItemStack.empty());
        }
    }
}
