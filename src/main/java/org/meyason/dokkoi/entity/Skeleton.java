package org.meyason.dokkoi.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.utilitem.Monei;

import java.util.UUID;

public class Skeleton extends GameEntity {

    private GameStatesManager manager;

    public Skeleton() {
        super(GameEntity.SKELETON);
        this.manager = Game.getInstance().getGameStatesManager();
    }

    public void kill(Entity entity, String enemyID) {
        manager.removeSpawnedEntity(enemyID);
        // 1パーセントの確率でモネイドロップ
        if(Math.random() <= 0.01){
            CustomItem moneiItem;
            try{
                moneiItem = GameItem.getItem(Monei.id);
            } catch (NoGameItemException e){
                return;
            }
            ItemStack item = moneiItem.getItem();
            // さらに1パーセントの確率で100モネイドロップ
            if(Math.random() <= 0.01) {
                item.setAmount(100);
            }else {
                item.setAmount(10);
            }
            entity.getWorld().dropItemNaturally(entity.getLocation(), item);
        }
        if(entity instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(0);
        }
    }

    public void knockback(Entity entity, Player player) {
        //もしほぼ同じ位置にいたらy軸に吹き飛ばす
        if(entity.getLocation().toVector().isInSphere(player.getLocation().toVector(), 0.5)) {
            player.setVelocity(new Vector(0, 1, 0).multiply(2));
            return;
        }
        Vector fromSkeletonToPlayer = player.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
        player.setVelocity(fromSkeletonToPlayer.multiply(3).setY(0.2));
    }

}
