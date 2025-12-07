package org.meyason.dokkoi.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
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

}
