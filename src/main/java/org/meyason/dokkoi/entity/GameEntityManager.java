package org.meyason.dokkoi.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityList;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.game.Game;

import java.util.Objects;
import java.util.UUID;

public class GameEntityManager {

    public GameEntityManager(){

    }

    public void registerEntity(){
        //TODO: それぞれスポーンさせる位置
        UUID uuid = Game.getInstance().getGameStatesManager().getAlivePlayers().get(0);
        Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
        Location location = player.getLocation();
        // Comedianはそれぞれ1体ずつスポーンさせる
        // Dealerは5体スポーンさせる
        // Clerkは3体スポーンさせる
        for (String comedianID : Comedian.comedianIDLIST){
            GameEntity gameEntity = GameEntity.getGameEntityFromId(comedianID);
            if(gameEntity instanceof Comedian comedian){
                spawnComedian(location, comedian);
            }
        }

        for (int i = 0; i < 5; i++){
            GameEntity gameEntity = GameEntity.getGameEntityFromId(GameEntity.DEALER);
            if(gameEntity instanceof Dealer dealer){
                spawnDealer(location, dealer);
            }
        }

        GameEntity gameEntity = GameEntity.getGameEntityFromId(GameEntity.CLERK);
        if(gameEntity instanceof Clerk clerk){
            spawnClerk(location, clerk);
        }

    }

    public void unregisterEntity(){
        // スポーンしている全てのエンティティを削除する
        for (Villager villager : Bukkit.getWorlds().get(0).getEntitiesByClass(Villager.class)){
            if(!villager.getPersistentDataContainer().isEmpty()){
                String npcName = villager.getPersistentDataContainer().get(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.NPC), PersistentDataType.STRING);
                String comedianName = villager.getPersistentDataContainer().get(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.COMEDIAN), PersistentDataType.STRING);
                if(npcName != null || comedianName != null){
                    killVillager(villager);
                }
            }
        }
    }

    public static boolean spawnEntityByID(Player player, String entityIDString){
        Location location = player.getLocation();
        GameEntityList gameEntityList = GameEntityList.getGameEntityList(entityIDString);
        if(gameEntityList == null){
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
        }else if(gameEntity instanceof Skeleton skeleton){
            spawnSkeleton(location, skeleton);
            player.sendMessage(Component.text("Skeleton spawned"));
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
        villager.setMaxHealth(1024.0);
        villager.setHealth(1024.0);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, Integer.MAX_VALUE));
        villager.setSilent(true);
//        villager.setInvulnerable(true);
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
        villager.setInvulnerable(true);
        String uuid = UUID.randomUUID().toString();
        villager.getPersistentDataContainer().set(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.NPC), PersistentDataType.STRING, uuid);
        Game.getInstance().getGameStatesManager().addSpawnedEntity(uuid, clerk);
    }

    public static void spawnSkeleton(Location location, Skeleton skeleton){
        String name = skeleton.getName();
        World world = location.getWorld();
        org.bukkit.entity.Skeleton bukkitSkeleton = (org.bukkit.entity.Skeleton) world.spawnEntity(location, EntityType.SKELETON);
        bukkitSkeleton.setCustomName(name);
        bukkitSkeleton.setCustomNameVisible(true);
        bukkitSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
        bukkitSkeleton.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
        bukkitSkeleton.getEquipment().setHelmet(new ItemStack(Material.AIR));
        bukkitSkeleton.getEquipment().setChestplate(new ItemStack(Material.AIR));
        bukkitSkeleton.getEquipment().setLeggings(new ItemStack(Material.AIR));
        bukkitSkeleton.getEquipment().setBoots(new ItemStack(Material.AIR));
        bukkitSkeleton.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.4D);
        bukkitSkeleton.getAttribute(Attribute.ATTACK_KNOCKBACK).setBaseValue(1.0D);
        bukkitSkeleton.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(0.0D);
        bukkitSkeleton.setMaxHealth(5.0);
        bukkitSkeleton.setHealth(5.0);
        bukkitSkeleton.setAggressive(true);
        String uuid = UUID.randomUUID().toString();
        bukkitSkeleton.getPersistentDataContainer().set(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.ENEMY), PersistentDataType.STRING, uuid);
        Game.getInstance().getGameStatesManager().addSpawnedEntity(uuid, skeleton);
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

    public static void killSkeleton(org.bukkit.entity.Skeleton skeleton){
        if(!skeleton.getPersistentDataContainer().isEmpty()){
            String enemyName = skeleton.getPersistentDataContainer().get(new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.ENEMY), PersistentDataType.STRING);
            if(enemyName != null){
                Game.getInstance().getGameStatesManager().removeSpawnedEntity(enemyName);
            }
        }
        skeleton.setHealth(0.0);
        skeleton.remove();
    }
}
