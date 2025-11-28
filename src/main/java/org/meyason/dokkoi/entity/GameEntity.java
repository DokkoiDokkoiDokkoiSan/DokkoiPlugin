package org.meyason.dokkoi.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.EntityID;

public class GameEntity {

    public static boolean spawnEntityByID(Player player, String entityIDString){
        Location location = player.getLocation();
        World world = location.getWorld();
        EntityID entityID = EntityID.getEntityID(entityIDString);
        if(entityID == null){
            player.sendMessage(Component.text("§4エンティティIDが不正です: " + entityIDString));
            return false;
        }
        String id = entityID.getId();
        String type = entityID.getType();
        if(type.equals("comedian")){
            Comedian comedian = Comedian.getComedianById(id);
            if(comedian != null){
                spawnComedian(location, comedian);
                player.sendMessage(Component.text("Comedian spawned"));
                return true;
            }else{
                player.sendMessage(Component.text("§4コメディアンIDが不正です: " + id));
                return false;
            }
        }
        return false;
    }

    public static void spawnComedian(Location location, Comedian comedian){
        String name = comedian.getName();
        String deathMessage = comedian.getDeathMessage();
        World world = location.getWorld();
        Villager villager = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        villager.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
        villager.getEquipment().setHelmet(new ItemStack(Material.AIR));
        villager.getEquipment().setChestplate(new ItemStack(Material.AIR));
        villager.getEquipment().setLeggings(new ItemStack(Material.AIR));
        villager.getEquipment().setBoots(new ItemStack(Material.AIR));
        villager.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.0D);
        villager.getAttribute(Attribute.ATTACK_KNOCKBACK).setBaseValue(0.0D);
        villager.setCollidable(false);
        villager.setSilent(true);
        villager.setInvulnerable(true);
        deathMessage = deathMessage == null ? "" : deathMessage;
        villager.getPersistentDataContainer().set(new NamespacedKey(Dokkoi.getInstance(), comedian.getId()), PersistentDataType.STRING, deathMessage);
    }

    public static void spawnDealer(Location location){
        String name = NPC.DEALER.getName();
        World world = location.getWorld();
        Villager villager = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
        villager.setCustomName(name);
        villager.setCustomNameVisible(true);
        villager.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        villager.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
    }

}
