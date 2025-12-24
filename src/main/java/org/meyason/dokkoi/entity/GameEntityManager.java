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
import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameEntityList;
import org.meyason.dokkoi.constants.GameEntityKeyString;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameLocation;

import java.util.Objects;
import java.util.UUID;

public class GameEntityManager {

    public GameEntityManager(){

    }

    public void registerEntity(){
        UUID uuid = Game.getInstance().getGameStatesManager().getAlivePlayers().get(0);
        Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
        World world = player.getWorld();

        for (String comedianID : Comedian.comedianIDLIST){
            GameEntity gameEntity = GameEntity.getGameEntityFromId(comedianID);
            if(gameEntity instanceof Comedian comedian){
                Location location = GameLocation.comedianLocations.get(comedianID).toLocation(world);
                spawnComedian(location, comedian);
            }
        }

        for (int i = 0; i < GameLocation.dealerLocations.size(); i++){
            GameEntity gameEntity = GameEntity.getGameEntityFromId(GameEntity.DEALER);
            if(gameEntity instanceof Dealer dealer){
                Location location = GameLocation.dealerLocations.get(i).toLocation(world);
                spawnDealer(location, dealer);
            }
        }

        for (int i = 0; i < GameLocation.clerkLocations.size(); i++) {
            Location location = GameLocation.clerkLocations.get(i).toLocation(world);
            GameEntity gameEntity;
            // TODO: 位置ベタ書き直す できれば全部違うキャラにしてmapで管理する
            if(location.toVector().equals(new Vector(144.5, 1, -139.5))){
                gameEntity = GameEntity.getGameEntityFromId(GameEntity.SUSURU);
            }else if(location.toVector().equals(new Vector(-142.5, 1, 140.5))) {
                gameEntity = GameEntity.getGameEntityFromId(GameEntity.INMU);
            }else {
                gameEntity = GameEntity.getGameEntityFromId(GameEntity.CLERK);
            }
            if (gameEntity instanceof Clerk clerk) {
                spawnClerk(location, clerk);
            }
        }

    }

    public void unregisterEntity(){
        World world = Bukkit.getWorld("world");
        if(world == null) return;

        Location loc1 = new Location(world, -151, 51, -151);
        Location loc2 = new Location(world, 151, 151, 151);

        for(org.bukkit.entity.Entity entity : world.getEntities()){
            if(entity instanceof Villager villager){
                String uuid = getEntityUUID(villager);
                if(uuid != null && Game.getInstance().getGameStatesManager().isExistsSpawnedEntityFromUUID(uuid)){
                    killVillager(villager);
                }
            } else if(entity instanceof org.bukkit.entity.Skeleton skeleton){
                String uuid = getEntityUUID(skeleton);
                if(uuid != null && Game.getInstance().getGameStatesManager().isExistsSpawnedEntityFromUUID(uuid)){
                    killSkeleton(skeleton);
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
        String name = GameEntity.clerkNameMap.get(clerk.getType());
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

    /**
     * 指定した中心位置と範囲内にあるGameEntityを取得
     * @param center 中心位置
     * @param radiusX X軸方向の半径
     * @param radiusY Y軸方向の半径
     * @param radiusZ Z軸方向の半径
     * @return 範囲内のGameEntityのリスト
     */
    public static java.util.List<GameEntity> getGameEntitiesInArea(Location center, double radiusX, double radiusY, double radiusZ){
        java.util.List<GameEntity> gameEntities = new java.util.ArrayList<>();
        World world = center.getWorld();
        if(world == null) return gameEntities;

        // 範囲内の全エンティティを取得
        for(org.bukkit.entity.Entity entity : world.getNearbyEntities(center, radiusX, radiusY, radiusZ)){
            if(entity instanceof Villager villager){
                String uuid = getEntityUUID(villager);
                if(uuid != null){
                    GameEntity gameEntity = Game.getInstance().getGameStatesManager().getSpawnedEntitiesFromUUID(uuid);
                    if(gameEntity != null){
                        gameEntities.add(gameEntity);
                    }
                }
            } else if(entity instanceof org.bukkit.entity.Skeleton skeleton){
                String uuid = getEntityUUID(skeleton);
                if(uuid != null){
                    GameEntity gameEntity = Game.getInstance().getGameStatesManager().getSpawnedEntitiesFromUUID(uuid);
                    if(gameEntity != null){
                        gameEntities.add(gameEntity);
                    }
                }
            }
        }
        return gameEntities;
    }

    /**
     * 2つの座標で定義される立方体空間内のGameEntityを取得
     * @param loc1 座標1
     * @param loc2 座標2
     * @return 範囲内のGameEntityのリスト
     */
    public static java.util.List<GameEntity> getGameEntitiesInCuboid(Location loc1, Location loc2){
        java.util.List<GameEntity> gameEntities = new java.util.ArrayList<>();
        World world = loc1.getWorld();
        if(world == null || !world.equals(loc2.getWorld())) return gameEntities;

        double minX = Math.min(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxX = Math.max(loc1.getX(), loc2.getX());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        // BoundingBoxを使って範囲内のエンティティを取得
        org.bukkit.util.BoundingBox boundingBox = new org.bukkit.util.BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

        for(org.bukkit.entity.Entity entity : world.getEntities()){
            if(boundingBox.contains(entity.getLocation().toVector())){
                if(entity instanceof Villager villager){
                    String uuid = getEntityUUID(villager);
                    if(uuid != null){
                        GameEntity gameEntity = Game.getInstance().getGameStatesManager().getSpawnedEntitiesFromUUID(uuid);
                        if(gameEntity != null){
                            gameEntities.add(gameEntity);
                        }
                    }
                } else if(entity instanceof org.bukkit.entity.Skeleton skeleton){
                    String uuid = getEntityUUID(skeleton);
                    if(uuid != null){
                        GameEntity gameEntity = Game.getInstance().getGameStatesManager().getSpawnedEntitiesFromUUID(uuid);
                        if(gameEntity != null){
                            gameEntities.add(gameEntity);
                        }
                    }
                }
            }
        }
        return gameEntities;
    }

    /**
     * エンティティからPersistentDataContainerに保存されているUUIDを取得
     * @param entity エンティティ
     * @return UUID文字列、存在しない場合はnull
     */
    private static String getEntityUUID(org.bukkit.entity.Entity entity){
        if(entity.getPersistentDataContainer().isEmpty()) return null;

        // NPCタイプをチェック
        String npcUUID = entity.getPersistentDataContainer().get(
            new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.NPC),
            PersistentDataType.STRING
        );
        if(npcUUID != null) return npcUUID;

        // コメディアンタイプをチェック
        String comedianUUID = entity.getPersistentDataContainer().get(
            new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.COMEDIAN),
            PersistentDataType.STRING
        );
        if(comedianUUID != null) return comedianUUID;

        // エネミータイプをチェック
        String enemyUUID = entity.getPersistentDataContainer().get(
            new NamespacedKey(Dokkoi.getInstance(), GameEntityKeyString.ENEMY),
            PersistentDataType.STRING
        );
        return enemyUUID;
    }
}
