package org.meyason.dokkoi.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
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

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public GameState getGameState() {return gameState;}

    public void setGameState(GameState gameState) {this.gameState = gameState;}

    public Game(){
        instance = this;
        init();
    }

    public void init(){
        scheduler = new Scheduler().runTaskTimer(Dokkoi.getInstance(), 0L, 20L);

        alivePlayers = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            alivePlayers.add(player);
        }

    }
}
