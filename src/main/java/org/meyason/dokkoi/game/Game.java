package org.meyason.dokkoi.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Game {

    private static Game instance;

    private GameState gameState;

    private BukkitTask scheduler;

    private List<Player> alivePlayers;
    private HashMap<Player, Goal> playerGoals;

    private final int minimumGameStartPlayers = 2;

    private int nowTime;
    public final int matchingPhaseTime = 30;
    public final int prepPhaseTime = 20;
    public final int gamePhaseTime = 600;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public GameState getGameState() {return gameState;}

    public void setGameState(GameState gameState) {this.gameState = gameState;}

    public List<Player> getAlivePlayers() {return alivePlayers;}
    public HashMap<Player, Goal> getPlayerGoals() {return playerGoals;}
    public int getNowTime() {return nowTime;}
    public void setNowTime(int nowTime) {this.nowTime = nowTime;}

    public Game(){
        instance = this;
        init();
    }

    public void init(){
        gameState = GameState.WAITING;
        playerGoals = new HashMap<>();
        alivePlayers = new ArrayList<>();
        setNowTime(matchingPhaseTime);
    }

    public void matching(){
        if(alivePlayers.size() < minimumGameStartPlayers){
            Component message = Component.text("§c参加者が最低人数に達していないため、ゲームを開始できません。");
            Bukkit.getServer().broadcast(message);
            this.gameState = GameState.WAITING;
            return;
        }
        Component message = Component.text("§aマッチングを開始。" + matchingPhaseTime + "秒後に目標が決定する。");
        Bukkit.getServer().broadcast(message);

        setGameState(GameState.STARTING);
        scheduler = new Scheduler().runTaskTimer(Dokkoi.getInstance(), 0L, 20L);

        for(Player player : Bukkit.getOnlinePlayers()){
            this.alivePlayers.add(player);
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setCustomNameVisible(false);
        }

        List<Goal> goalList = new ArrayList<>(GoalList.getAllGoals());
        for(Player player : alivePlayers){
            int randomIndex = (int)(Math.random() * goalList.size());
            Goal goal = goalList.get(randomIndex);
            goal.setGoal(this, player);
            playerGoals.put(player, goal);
        }

    }

    public void prepPhase(){
        setGameState(GameState.PREP);
        setNowTime(prepPhaseTime);
        // TODO:プレイヤーのテレポート
        Component message = Component.text("§a準備フェーズが開始。各自目標に備え準備せよ！");
        message.append(Component.text("\n§e" + prepPhaseTime + "秒後にゲームが開始"));
        Bukkit.getServer().broadcast(message);

        for (Player player : alivePlayers) {
            Goal goal = playerGoals.get(player);
            player.sendMessage("§b目標：§e " + goal.getDescription());
        }
        // TODO:攻撃イベント、クリックイベントの無効化
    }

    public void startGame(){
        setGameState(GameState.IN_GAME);

    }
}
