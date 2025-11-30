package org.meyason.dokkoi.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityList;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.game.Game;

import java.util.Objects;
import java.util.UUID;

public class GameEntityManager {

    public static boolean spawnEntityByID(Player player, String entityIDString){
        Location location = player.getLocation();
        GameEntityList gameEntityList = GameEntityList.getGameEntityList(entityIDString);
        if(gameEntityList == null){
            player.sendMessage(Component.text("§4エンティティIDが不正です: " + entityIDString));
            return false;
        }
        String id = gameEntityList.getId();
        GameEntity gameEntity = GameEntity.getGameEntityFromId(id);
        if(gameEntity instanceof Comedian comedian){
            spawnComedian(location, comedian);
            player.sendMessage(Component.text("Comedian spawned"));
            return true;
        }else if(gameEntity instanceof Dealer dealer){
            spawnDealer(location, dealer);
            player.sendMessage(Component.text("Dealer spawned"));
            return true;
        }else if(gameEntity instanceof Clerk clerk){
            spawnClerk(location, clerk);
            player.sendMessage(Component.text("Clerk spawned"));
            return true;
        }
        return false;
    }

    public static void spawnComedian(Location location, Comedian comedian){
        String name = comedian.getName();
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
        villager.setProfession(Villager.Profession.NONE);
        villager.setCollidable(false);
        villager.setSilent(true);
        villager.setInvulnerable(true);
        String uuid = UUID.randomUUID().toString();
        villager.getPersistentDataContainer().set(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.COMEDIAN), PersistentDataType.STRING, uuid);
        Game.getInstance().getGameStatesManager().addSpawnedEntity(uuid, comedian);
    }

    public static void spawnDealer(Location location, Dealer dealer){
        String name = dealer.getName();
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
        villager.setProfession(Villager.Profession.WEAPONSMITH);
        villager.setCollidable(false);
        villager.setInvulnerable(true);
        String uuid = UUID.randomUUID().toString();
        villager.getPersistentDataContainer().set(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.NPC), PersistentDataType.STRING, uuid);
        Game.getInstance().getGameStatesManager().addSpawnedEntity(uuid, dealer);
    }

    public static void spawnClerk(Location location, Clerk clerk){
        String name = clerk.getName();
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
        villager.setProfession(Villager.Profession.LIBRARIAN);
        villager.setCollidable(false);
        villager.setInvulnerable(true);
        String uuid = UUID.randomUUID().toString();
        villager.getPersistentDataContainer().set(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.NPC), PersistentDataType.STRING, uuid);
        Game.getInstance().getGameStatesManager().addSpawnedEntity(uuid, clerk);
    }

    public static void killVillager(Villager villager){
        if(!villager.getPersistentDataContainer().isEmpty()){
            String npcName = villager.getPersistentDataContainer().get(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.NPC), PersistentDataType.STRING);
            if(npcName != null){
                Game.getInstance().getGameStatesManager().removeSpawnedEntity(npcName);
            }
            String comedianName = villager.getPersistentDataContainer().get(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.COMEDIAN), PersistentDataType.STRING);
            if(comedianName != null){
                Game.getInstance().getGameStatesManager().removeSpawnedEntity(comedianName);
            }
        }
        villager.setHealth(0.0);
        villager.remove();
    }
}
