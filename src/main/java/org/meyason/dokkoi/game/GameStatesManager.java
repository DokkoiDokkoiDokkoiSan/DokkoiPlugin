package org.meyason.dokkoi.game;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Trident;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.entity.GameEntity;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.job.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameStatesManager {

    private Game game;

    private GameState gameState;

    private List<UUID> alivePlayers;
    private List<UUID> joinedPlayers;
    private HashMap<UUID, Goal> playerGoals;
    private HashMap<UUID, Job> playerJobs;
    private HashMap<UUID, UUID> killerList;
    private HashMap<UUID, Integer> killCounts;
    private List<UUID> attackedPlayers;
    private List<UUID> damagedPlayers;
    private HashMap<Entity, ProjectileData> projectileDataMap;
    private HashMap<UUID, Integer> additionalDamage;
    private HashMap<UUID, Integer> damageCutPercent;
    private HashMap<UUID, Boolean> isDeactivateDamageOnce;
    private List<UUID> onDisablePotionEffectPlayers;

    private HashMap<String, CustomItem> serialCustomItemMap;

    private HashMap<UUID, BukkitRunnable> skillCoolDownTasks;
    private HashMap<UUID, BukkitRunnable> ultimateSkillCoolDownTasks;
    private HashMap<UUID, BukkitRunnable> coolDownScheduler;
    private HashMap<UUID, BukkitRunnable> itemCoolDownScheduler;
    private HashMap<Trident, BukkitRunnable> tridentDespawnWatchDogs;

    private HashMap<String, GameEntity> spawnedEntities;

    private boolean isEnableKillerList = false;

    public GameStatesManager(Game game) {
        this.game = game;
        init();
    }

    public void init(){
        this.gameState = GameState.WAITING;
        this.isEnableKillerList = false;
        alivePlayers = new ArrayList<>();
        joinedPlayers = new ArrayList<>();
        playerGoals = new HashMap<>();
        playerJobs = new HashMap<>();
        killerList = new HashMap<>();
        killCounts = new HashMap<>();
        attackedPlayers = new ArrayList<>();
        damagedPlayers = new ArrayList<>();
        projectileDataMap = new HashMap<>();
        additionalDamage = new HashMap<>();
        damageCutPercent = new HashMap<>();
        isDeactivateDamageOnce = new HashMap<>();
        onDisablePotionEffectPlayers = new ArrayList<>();
        serialCustomItemMap = new HashMap<>();
        skillCoolDownTasks = new HashMap<>();
        ultimateSkillCoolDownTasks = new HashMap<>();
        coolDownScheduler = new HashMap<>();
        itemCoolDownScheduler = new HashMap<>();
        tridentDespawnWatchDogs = new HashMap<>();
        spawnedEntities = new HashMap<>();
    }

    public void clearAll(){
        alivePlayers.clear();
        joinedPlayers.clear();
        playerGoals.clear();
        playerJobs.clear();
        killerList.clear();
        killCounts.clear();
        attackedPlayers.clear();
        damagedPlayers.clear();
        projectileDataMap.clear();
        additionalDamage.clear();
        damageCutPercent.clear();
        isDeactivateDamageOnce.clear();
        onDisablePotionEffectPlayers.clear();
        serialCustomItemMap.clear();
        skillCoolDownTasks.clear();
        ultimateSkillCoolDownTasks.clear();
        coolDownScheduler.clear();
        itemCoolDownScheduler.clear();
        tridentDespawnWatchDogs.clear();
        spawnedEntities.clear();
    }

    public void removePlayerData(UUID uuid){
        removeAlivePlayer(uuid);
        removeJoinedPlayer(uuid);
        removePlayerGoal(uuid);
        removePlayerJob(uuid);
        removeKiller(uuid);
        removeKillCount(uuid);
        removeAttackedPlayer(uuid);
        removeDamagedPlayer(uuid);
        removeAdditionalDamage(uuid);
        removeDamageCutPercent(uuid);
        removeIsDeactivateDamageOnce(uuid);
        removeOnDisablePotionEffectPlayer(uuid);
        removeSkillCoolDownTask(uuid);
        removeUltimateSkillCoolDownTask(uuid);
        removeCoolDownScheduler(uuid);
        removeItemCoolDownScheduler(uuid);
    }

    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {this.gameState = gameState;}

    public boolean isEnableKillerList() {return isEnableKillerList;}
    public void setEnableKillerList(boolean enableKillerList) {isEnableKillerList = enableKillerList;}

    public List<UUID> getAlivePlayers() {return alivePlayers;}
    public void setAlivePlayers(List<UUID> alivePlayers) {this.alivePlayers = alivePlayers;}
    public void addAlivePlayer(UUID playerUUID) {this.alivePlayers.add(playerUUID);}
    public void removeAlivePlayer(UUID playerUUID) {
        if (!this.alivePlayers.contains(playerUUID)) {return;}
        this.alivePlayers.remove(playerUUID);
    }

    public List<UUID> getJoinedPlayers() {return joinedPlayers;}
    public void setJoinedPlayers(List<UUID> joinedPlayers) {this.joinedPlayers = joinedPlayers;}
    public void addJoinedPlayer(UUID uuid) {this.joinedPlayers.add(uuid);}
    public void removeJoinedPlayer(UUID uuid) {
        if(!this.joinedPlayers.contains(uuid)) {return;}
        this.joinedPlayers.remove(uuid);
    }

    public HashMap<UUID, Goal> getPlayerGoals() {return playerGoals;}
    public void setPlayerGoals(HashMap<UUID, Goal> playerGoals) {this.playerGoals = playerGoals;}
    public void addPlayerGoal(UUID uuid, Goal goal) {this.playerGoals.put(uuid, goal);}
    public void removePlayerGoal(UUID uuid) {
        if (!this.playerGoals.containsKey(uuid)) {return;}
        this.playerGoals.remove(uuid);
    }

    public HashMap<UUID, Job> getPlayerJobs() {return playerJobs;}
    public void setPlayerJobs(HashMap<UUID, Job> playerJobs) {this.playerJobs = playerJobs;}
    public void addPlayerJob(UUID uuid, Job job) {this.playerJobs.put(uuid, job);}
    public void removePlayerJob(UUID uuid) {
        if (!this.playerJobs.containsKey(uuid)) {return;}
        this.playerJobs.remove(uuid);
    }

    public HashMap<UUID, UUID> getKillerList() {return killerList;}
    public void setKillerList(HashMap<UUID, UUID> killerList) {this.killerList = killerList;}
    public void addKiller(UUID killer, UUID victim) {this.killerList.put(killer, victim);}
    public void removeKiller(UUID killer) {
        if (!this.killerList.containsKey(killer)) {return;}
        this.killerList.remove(killer);
    }

    public HashMap<UUID, Integer> getKillCounts() {return killCounts;}
    public void setKillCounts(HashMap<UUID, Integer> killCounts) {this.killCounts = killCounts;}
    public void addKillCount(UUID player) {
        this.killCounts.put(player, this.killCounts.getOrDefault(player, 0) + 1);
    }
    public void removeKillCount(UUID player) {
        if(!this.killCounts.containsKey(player)) return;
        this.killCounts.remove(player);
    }

    public List<UUID> getAttackedPlayers() {return attackedPlayers;}
    public void setAttackedPlayers(List<UUID> attackedPlayers) {this.attackedPlayers = attackedPlayers;}
    public void addAttackedPlayer(UUID player) {
        if(this.attackedPlayers.contains(player)) return;
        this.attackedPlayers.add(player);
    }
    public void removeAttackedPlayer(UUID player) {
        if(!this.attackedPlayers.contains(player)) return;
        this.attackedPlayers.remove(player);
    }

    public List<UUID> getDamagedPlayers() {return damagedPlayers;}
    public void setDamagedPlayers(List<UUID> damagedPlayers) {this.damagedPlayers = damagedPlayers;}
    public void addDamagedPlayer(UUID player) {
        if(this.damagedPlayers.contains(player)) return;
        this.damagedPlayers.add(player);
    }
    public void removeDamagedPlayer(UUID player) {
        if(this.damagedPlayers.contains(player)) return;
        this.damagedPlayers.remove(player);
    }

    public HashMap<Entity, ProjectileData> getProjectileDataMap() {return projectileDataMap;}
    public void setProjectileDataMap(HashMap<Entity, ProjectileData> projectileDataMap) {this.projectileDataMap = projectileDataMap;}
    public void addProjectileData(Entity entity, ProjectileData data) {this.projectileDataMap.put(entity, data);}
    public void removeProjectileData(Entity entity) {this.projectileDataMap.remove(entity);}

    public HashMap<UUID, BukkitRunnable> getSkillCoolDownTasks() {return skillCoolDownTasks;}
    public void setSkillCoolDownTasks(HashMap<UUID, BukkitRunnable> skillCoolDownTasks) {this.skillCoolDownTasks = skillCoolDownTasks;}
    public void addSkillCoolDownTask(UUID player, BukkitRunnable task) {this.skillCoolDownTasks.put(player, task);}
    public void removeSkillCoolDownTask(UUID player) {
        if(!this.skillCoolDownTasks.containsKey(player)) {return;}
        this.skillCoolDownTasks.remove(player);
    }

    public HashMap<UUID, Integer> getAdditionalDamage() {return additionalDamage;}
    public void setAdditionalDamage(HashMap<UUID, Integer> additionalDamage) {this.additionalDamage = additionalDamage;}
    public void addAdditionalDamage(UUID player, int damage) {
        this.additionalDamage.put(player, this.additionalDamage.getOrDefault(player, 0) + damage);
    }
    public void removeAdditionalDamage(UUID player) {
        if(!this.additionalDamage.containsKey(player)) {return;}
        this.additionalDamage.remove(player);
    }

    public HashMap<UUID, Integer> getDamageCutPercent() {return damageCutPercent;}
    public void setDamageCutPercent(HashMap<UUID, Integer> damageCutPercent) {this.damageCutPercent = damageCutPercent;}
    public void addDamageCutPercent(UUID player, int percent) {
        this.damageCutPercent.put(player, percent);
    }
    public void calcDamageCutPercent(UUID player, int percent) {
        this.damageCutPercent.put(player, this.damageCutPercent.getOrDefault(player, 0) + percent);
    }
    public void removeDamageCutPercent(UUID player) {
        if(!this.damageCutPercent.containsKey(player)) {return;}
        this.damageCutPercent.remove(player);
    }

    public HashMap<UUID, Boolean> getIsDeactivateDamageOnce() {return isDeactivateDamageOnce;}
    public void setIsDeactivateDamageOnce(HashMap<UUID, Boolean> isDeactivateDamageOnce) {this.isDeactivateDamageOnce = isDeactivateDamageOnce;}
    public void addIsDeactivateDamageOnce(UUID player, boolean value) {
        this.isDeactivateDamageOnce.put(player, value);
    }
    public void removeIsDeactivateDamageOnce(UUID player) {
        if(!this.isDeactivateDamageOnce.containsKey(player)) {return;}
        this.isDeactivateDamageOnce.remove(player);
    }

    public boolean isInOnDisablePotionEffectPlayers(UUID player) {
        return onDisablePotionEffectPlayers.contains(player);
    }
    public void addOnDisablePotionEffectPlayer(UUID player) {
        if(this.onDisablePotionEffectPlayers.contains(player)) return;
        this.onDisablePotionEffectPlayers.add(player);
    }
    public void removeOnDisablePotionEffectPlayer(UUID player) {
        if(!this.onDisablePotionEffectPlayers.contains(player)) return;
        this.onDisablePotionEffectPlayers.remove(player);
    }

    public CustomItem getCustomItemFromSerial(String uuid) {return serialCustomItemMap.get(uuid);}
    public boolean isExistsCustomItemFromSerial(String uuid) {return serialCustomItemMap.containsKey(uuid);}
    public void addCustomItemToSerialMap(String uuid, CustomItem customItem) {this.serialCustomItemMap.put(uuid, customItem);}
    public void removeCustomItemFromSerialMap(String uuid) {
        if(!this.serialCustomItemMap.containsKey(uuid)) {return;}
        this.serialCustomItemMap.remove(uuid);
    }


    public HashMap<UUID, BukkitRunnable> getUltimateSkillCoolDownTasks() {return ultimateSkillCoolDownTasks;}
    public void setUltimateSkillCoolDownTasks(HashMap<UUID, BukkitRunnable> ultimateSkillCoolDownTasks) {this.ultimateSkillCoolDownTasks = ultimateSkillCoolDownTasks;}
    public void addUltimateSkillCoolDownTask(UUID player, BukkitRunnable task) {this.ultimateSkillCoolDownTasks.put(player, task);}
    public void removeUltimateSkillCoolDownTask(UUID player) {
        if(!this.ultimateSkillCoolDownTasks.containsKey(player)) {return;}
        this.ultimateSkillCoolDownTasks.remove(player);
    }

    public HashMap<UUID, BukkitRunnable> getCoolDownScheduler() {return coolDownScheduler;}
    public void setCoolDownScheduler(HashMap<UUID, BukkitRunnable> coolDownScheduler) {this.coolDownScheduler = coolDownScheduler;}
    public void addCoolDownScheduler(UUID player, BukkitRunnable task) {this.coolDownScheduler.put(player, task);}
    public void removeCoolDownScheduler(UUID player) {
        if(!this.coolDownScheduler.containsKey(player)) {return;}
        this.coolDownScheduler.remove(player);
    }

    public HashMap<UUID, BukkitRunnable> getItemCoolDownScheduler() {return itemCoolDownScheduler;}
    public void setItemCoolDownScheduler(HashMap<UUID, BukkitRunnable> itemCoolDownScheduler) {this.itemCoolDownScheduler = itemCoolDownScheduler;}
    public void addItemCoolDownScheduler(UUID player, BukkitRunnable task) {this.itemCoolDownScheduler.put(player, task);}
    public void removeItemCoolDownScheduler(UUID player) {
        if(!this.itemCoolDownScheduler.containsKey(player)) {return;}
        this.itemCoolDownScheduler.remove(player);
    }

    public HashMap<Trident, BukkitRunnable> getTridentDespawnWatchDogs() {return tridentDespawnWatchDogs;}
    public void setTridentDespawnWatchDogs(HashMap<Trident, BukkitRunnable> tridentDespawnWatchDogs) {this.tridentDespawnWatchDogs = tridentDespawnWatchDogs;}
    public void addTridentDespawnWatchDog(Trident trident, BukkitRunnable task) {this.tridentDespawnWatchDogs.put(trident, task);}
    public void removeTridentDespawnWatchDog(Trident trident) {
        if(!this.tridentDespawnWatchDogs.containsKey(trident)) {return;}
        this.tridentDespawnWatchDogs.remove(trident);
    }

    public GameEntity getSpawnedEntitiesFromUUID(String uuid) {return spawnedEntities.get(uuid);}
    public boolean isExistsSpawnedEntityFromUUID(String uuid) {return spawnedEntities.containsKey(uuid);}
    public void addSpawnedEntity(String uuid, GameEntity entity) {this.spawnedEntities.put(uuid, entity);}
    public void removeSpawnedEntity(String uuid) {
        if (!isExistsSpawnedEntityFromUUID(uuid)) {
            return;
        }
        this.spawnedEntities.remove(uuid);
    }
}
