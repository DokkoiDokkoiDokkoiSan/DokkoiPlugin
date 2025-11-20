package org.meyason.dokkoi.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game {

    private static Game instance;

    private GameState gameState;

    private BukkitTask scheduler;

    private List<Player> alivePlayers;
    private List<Player> joinedPlayers;
    private HashMap<Player, Goal> playerGoals;
    private HashMap<Player, Player> killerList;

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
    public HashMap<Player, Player> getKillerList() {return killerList;}
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
        killerList = new HashMap<>();
        setNowTime(matchingPhaseTime);
    }

    public void matching(){
        if(alivePlayers.size() < minimumGameStartPlayers){
            Component message = Component.text("§c参加者が最低人数(" + minimumGameStartPlayers + "人)に達していないため、ゲームを開始できません。");
            Bukkit.getServer().broadcast(message);
            this.gameState = GameState.WAITING;
            return;
        }
        Component message = Component.text("§aマッチングを開始。" + matchingPhaseTime + "秒後に目標が決定する。");
        Bukkit.getServer().broadcast(message);

        setGameState(GameState.MATCHING);
        scheduler = new Scheduler().runTaskTimer(Dokkoi.getInstance(), 0L, 20L);

        for(Player player : Bukkit.getOnlinePlayers()){
            this.alivePlayers.add(player);
            this.joinedPlayers.add(player);
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setCustomNameVisible(false);
        }

        List<Goal> goalList = new ArrayList<>(GoalList.getAllGoals());
        for(Player player : joinedPlayers) {
            int randomIndex = (int) (Math.random() * goalList.size());
            Goal goal = goalList.get(randomIndex);
            goal.setGoal(this, player);
            playerGoals.put(player, goal);
        }
        Bukkit.getServer().broadcast(Component.text("§aマッチング完了。準備フェーズに移行します。"));
    }

    public void prepPhase(){
        setGameState(GameState.PREP);
        setNowTime(prepPhaseTime);
        // TODO:プレイヤーのテレポート
        Component message = Component.text("§a準備フェーズが開始。各自目標に備え準備せよ！");
        message.append(Component.text("\n§e" + prepPhaseTime + "秒後にゲームが開始"));
        Bukkit.getServer().broadcast(message);

        for (Player player : joinedPlayers) {
            Goal goal = playerGoals.get(player);
            player.sendMessage("§b一度しか言わないぞ。お前の目標は「§6" + goal.getDescription() + "§b」だ。");
        }
        // TODO:攻撃イベント、クリックイベントの無効化
    }

    public void startGame(){
        setGameState(GameState.IN_GAME);
        setNowTime(gamePhaseTime);

    }

    public void endGame(){
        setGameState(GameState.END);
        Component message = Component.text("§aゲーム終了");
        Bukkit.getServer().broadcast(message);
        List<Player> clearPlayers = new ArrayList<>();
        for(Player player : joinedPlayers){
            if(getPlayerGoals().get(player).isAchieved()){
                player.sendMessage("§aお前は目標を達成した！");
                clearPlayers.add(player);
            }else{
                player.sendMessage("§cお前は目標を達成できなかった...");
            }
        }
        if(clearPlayers.isEmpty()){
            Bukkit.getServer().broadcast(Component.text("§c誰も目標を達成できなかった..."));
        }else{
            StringBuilder clearPlayerNames = new StringBuilder();
            for(int i = 0; i < clearPlayers.size(); i++){
                clearPlayerNames.append(clearPlayers.get(i).getName());
                clearPlayerNames.append(": ");
                clearPlayerNames.append(getPlayerGoals().get(clearPlayers.get(i)).getName());
                if(i < clearPlayers.size() - 1){
                    clearPlayerNames.append("\n");
                }
            }
            Bukkit.getServer().broadcast(Component.text("§a目標を達成したプレイヤー\n §e" + clearPlayerNames));
        }
        clearScoreboardDisplay();
    }

    public void resetGame(){
        scheduler.cancel();
        setGameState(GameState.WAITING);
        for(Player player : joinedPlayers){
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setCustomNameVisible(true);
        }
        new Game();
    }

    public void updateScoreboardDisplay(){
        Bukkit.getOnlinePlayers().forEach(this::updateScoreboardDisplay);
    }

    public void updateScoreboardDisplay(Player player){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("DokkoiGame", "dummy", Component.text("§aステータス： " + gameState.getDisplayName()));
        objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        int i = 0;

        if(getGameState() == GameState.MATCHING){
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a参加者数: §f" + Bukkit.getOnlinePlayers().size() + "人").setScore(--i);
        }else if(getGameState() == GameState.PREP || getGameState() == GameState.IN_GAME){
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a生存者数: §f" + getAlivePlayers().size() + "人").setScore(--i);
            objective.getScore("§e目標: §f" + playerGoals.get(player).getName()).setScore(--i);
        }
    }

    public void clearScoreboardDisplay(){
        Bukkit.getOnlinePlayers().forEach(this::clearScoreboardDisplay);
    }

    public void clearScoreboardDisplay(Player player){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        player.setScoreboard(scoreboard);
    }
}
