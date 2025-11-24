package org.meyason.dokkoi.game;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.job.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameStatesManager {

    private Game game;

    private GameState gameState;

    private List<Player> alivePlayers;
    private List<Player> joinedPlayers;
    private HashMap<Player, Goal> playerGoals;
    private HashMap<Player, Job> playerJobs;
    private HashMap<Player, Player> killerList;
    private HashMap<Player, String> goalFixedPlayers;
    private HashMap<Player, Integer> killCounts;
    private List<Player> attackedPlayers;
    private List<Player> damagedPlayers;
    private HashMap<Entity, ProjectileData> projectileDataMap;

    private HashMap<Player, BukkitRunnable> skillCoolDownTasks;
    private HashMap<Player, BukkitRunnable> ultimateSkillCoolDownTasks;
    private HashMap<Player, BukkitRunnable> coolDownScheduler;
    private HashMap<Player, BukkitRunnable> itemCoolDownScheduler;
    private HashMap<Trident, BukkitRunnable> tridentDespawnWatchDogs;

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
        goalFixedPlayers = new HashMap<>();
        killCounts = new HashMap<>();
        attackedPlayers = new ArrayList<>();
        damagedPlayers = new ArrayList<>();
        projectileDataMap = new HashMap<>();
        skillCoolDownTasks = new HashMap<>();
        ultimateSkillCoolDownTasks = new HashMap<>();
        coolDownScheduler = new HashMap<>();
        itemCoolDownScheduler = new HashMap<>();
        tridentDespawnWatchDogs = new HashMap<>();
    }

    public void clearAll(){
        alivePlayers.clear();
        joinedPlayers.clear();
        playerGoals.clear();
        playerJobs.clear();
        killerList.clear();
        goalFixedPlayers.clear();
        killCounts.clear();
        attackedPlayers.clear();
        damagedPlayers.clear();
        projectileDataMap.clear();
        skillCoolDownTasks.clear();
        ultimateSkillCoolDownTasks.clear();
        coolDownScheduler.clear();
        itemCoolDownScheduler.clear();
        tridentDespawnWatchDogs.clear();
    }

    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {this.gameState = gameState;}

    public boolean isEnableKillerList() {return isEnableKillerList;}
    public void setEnableKillerList(boolean enableKillerList) {isEnableKillerList = enableKillerList;}

    public List<Player> getAlivePlayers() {return alivePlayers;}
    public void setAlivePlayers(List<Player> alivePlayers) {this.alivePlayers = alivePlayers;}
    public void addAlivePlayer(Player player) {this.alivePlayers.add(player);}
    public void removeAlivePlayer(Player player) {this.alivePlayers.remove(player);}

    public List<Player> getJoinedPlayers() {return joinedPlayers;}
    public void setJoinedPlayers(List<Player> joinedPlayers) {this.joinedPlayers = joinedPlayers;}
    public void addJoinedPlayer(Player player) {this.joinedPlayers.add(player);}
    public void removeJoinedPlayer(Player player) {this.joinedPlayers.remove(player);}

    public HashMap<Player, Goal> getPlayerGoals() {return playerGoals;}
    public void setPlayerGoals(HashMap<Player, Goal> playerGoals) {this.playerGoals = playerGoals;}
    public void addPlayerGoal(Player player, Goal goal) {this.playerGoals.put(player, goal);}
    public void removePlayerGoal(Player player) {this.playerGoals.remove(player);}

    public HashMap<Player, Job> getPlayerJobs() {return playerJobs;}
    public void setPlayerJobs(HashMap<Player, Job> playerJobs) {this.playerJobs = playerJobs;}
    public void addPlayerJob(Player player, Job job) {this.playerJobs.put(player, job);}
    public void removePlayerJob(Player player) {this.playerJobs.remove(player);}

    public HashMap<Player, Player> getKillerList() {return killerList;}
    public void setKillerList(HashMap<Player, Player> killerList) {this.killerList = killerList;}
    public void addKiller(Player killer, Player victim) {this.killerList.put(killer, victim);}
    public void removeKiller(Player killer) {this.killerList.remove(killer);}

    public HashMap<Player, String> getGoalFixedPlayers() {return goalFixedPlayers;}
    public void setGoalFixedPlayers(HashMap<Player, String> goalFixedPlayers) {this.goalFixedPlayers = goalFixedPlayers;}
    public void addGoalFixedPlayer(Player player, String goal) {this.goalFixedPlayers.put(player, goal);}
    public void removeGoalFixedPlayer(Player player) {this.goalFixedPlayers.remove(player);}

    public HashMap<Player, Integer> getKillCounts() {return killCounts;}
    public void setKillCounts(HashMap<Player, Integer> killCounts) {this.killCounts = killCounts;}
    public void addKillCount(Player player) {
        this.killCounts.put(player, this.killCounts.getOrDefault(player, 0) + 1);
    }
    public void removeKillCount(Player player) {this.killCounts.remove(player);}

    public List<Player> getAttackedPlayers() {return attackedPlayers;}
    public void setAttackedPlayers(List<Player> attackedPlayers) {this.attackedPlayers = attackedPlayers;}
    public void addAttackedPlayer(Player player) {
        if(this.attackedPlayers.contains(player)) return;
        this.attackedPlayers.add(player);
    }
    public void removeAttackedPlayer(Player player) {
        if(!this.attackedPlayers.contains(player)) return;
        this.attackedPlayers.remove(player);
    }

    public List<Player> getDamagedPlayers() {return damagedPlayers;}
    public void setDamagedPlayers(List<Player> damagedPlayers) {this.damagedPlayers = damagedPlayers;}
    public void addDamagedPlayer(Player player) {
        if(this.damagedPlayers.contains(player)) return;
        this.damagedPlayers.add(player);
    }
    public void removeDamagedPlayer(Player player) {
        if(this.damagedPlayers.contains(player)) return;
        this.damagedPlayers.remove(player);
    }

    public HashMap<Entity, ProjectileData> getProjectileDataMap() {return projectileDataMap;}
    public void setProjectileDataMap(HashMap<Entity, ProjectileData> projectileDataMap) {this.projectileDataMap = projectileDataMap;}
    public void addProjectileData(Entity entity, ProjectileData data) {this.projectileDataMap.put(entity, data);}
    public void removeProjectileData(Entity entity) {this.projectileDataMap.remove(entity);}

    public HashMap<Player, BukkitRunnable> getSkillCoolDownTasks() {return skillCoolDownTasks;}
    public void setSkillCoolDownTasks(HashMap<Player, BukkitRunnable> skillCoolDownTasks) {this.skillCoolDownTasks = skillCoolDownTasks;}
    public void addSkillCoolDownTask(Player player, BukkitRunnable task) {this.skillCoolDownTasks.put(player, task);}
    public void removeSkillCoolDownTask(Player player) {this.skillCoolDownTasks.remove(player);}

    public HashMap<Player, BukkitRunnable> getUltimateSkillCoolDownTasks() {return ultimateSkillCoolDownTasks;}
    public void setUltimateSkillCoolDownTasks(HashMap<Player, BukkitRunnable> ultimateSkillCoolDownTasks) {this.ultimateSkillCoolDownTasks = ultimateSkillCoolDownTasks;}
    public void addUltimateSkillCoolDownTask(Player player, BukkitRunnable task) {this.ultimateSkillCoolDownTasks.put(player, task);}
    public void removeUltimateSkillCoolDownTask(Player player) {this.ultimateSkillCoolDownTasks.remove(player);}

    public HashMap<Player, BukkitRunnable> getCoolDownScheduler() {return coolDownScheduler;}
    public void setCoolDownScheduler(HashMap<Player, BukkitRunnable> coolDownScheduler) {this.coolDownScheduler = coolDownScheduler;}
    public void addCoolDownScheduler(Player player, BukkitRunnable task) {this.coolDownScheduler.put(player, task);}
    public void removeCoolDownScheduler(Player player) {this.coolDownScheduler.remove(player);}

    public HashMap<Player, BukkitRunnable> getItemCoolDownScheduler() {return itemCoolDownScheduler;}
    public void setItemCoolDownScheduler(HashMap<Player, BukkitRunnable> itemCoolDownScheduler) {this.itemCoolDownScheduler = itemCoolDownScheduler;}
    public void addItemCoolDownScheduler(Player player, BukkitRunnable task) {this.itemCoolDownScheduler.put(player, task);}
    public void removeItemCoolDownScheduler(Player player) {this.itemCoolDownScheduler.remove(player);}

    public HashMap<Trident, BukkitRunnable> getTridentDespawnWatchDogs() {return tridentDespawnWatchDogs;}
    public void setTridentDespawnWatchDogs(HashMap<Trident, BukkitRunnable> tridentDespawnWatchDogs) {this.tridentDespawnWatchDogs = tridentDespawnWatchDogs;}
    public void addTridentDespawnWatchDog(Trident trident, BukkitRunnable task) {this.tridentDespawnWatchDogs.put(trident, task);}
    public void removeTridentDespawnWatchDog(Trident trident) {this.tridentDespawnWatchDogs.remove(trident);}
}
