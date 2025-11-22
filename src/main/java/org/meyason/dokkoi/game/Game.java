package org.meyason.dokkoi.game;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.*;
import org.meyason.dokkoi.goal.GachaAddict;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.scheduler.Scheduler;
import org.meyason.dokkoi.scheduler.SkillScheduler;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private static Game instance;

    private GameStatesManager gameStatesManager;

    private BukkitTask scheduler;

    private final int minimumGameStartPlayers = 2;

    private int nowTime;
    public final int matchingPhaseTime = 5;
    public final int prepPhaseTime = 5;
    public final int gamePhaseTime = 300;
    public final int resultPhaseTime = 10;

    private final boolean debugMode = false;
    private boolean onGame = false;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public int getNowTime() {return nowTime;}
    public void setNowTime(int nowTime) {this.nowTime = nowTime;}
    public GameStatesManager getGameStatesManager() {return gameStatesManager;}

    public Game(){
        instance = this;
        this.gameStatesManager = new GameStatesManager(this);
        init();
    }

    public void init(){
        gameStatesManager.init();
        setNowTime(matchingPhaseTime);
    }

    public void matching(){
        if(Bukkit.getOnlinePlayers().size() < minimumGameStartPlayers){
            Component message = Component.text("§c参加者が最低人数(" + minimumGameStartPlayers + "人)に達していないため、ゲームを開始できません。");
            Bukkit.getServer().broadcast(message);
            gameStatesManager.setGameState(GameState.WAITING);
            return;
        }
        Component message = Component.text("§aマッチングを開始。" + matchingPhaseTime + "秒後に目標が決定する。");
        Bukkit.getServer().broadcast(message);

        gameStatesManager.setGameState(GameState.MATCHING);
        scheduler = new Scheduler().runTaskTimer(Dokkoi.getInstance(), 0L, 20L);

        updateScoreboardDisplay();

        for(Player player : Bukkit.getOnlinePlayers()){
            gameStatesManager.addAlivePlayer(player);
            gameStatesManager.addJoinedPlayer(player);
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setCustomNameVisible(false);
        }

        List<Job> jobList = new ArrayList<>(JobList.getAllJobs());
        for(Player player : gameStatesManager.getJoinedPlayers()) {
            int randomIndex = (int) (Math.random() * jobList.size());
            Job job = jobList.get(randomIndex).clone();
            gameStatesManager.getPlayerJobs().put(player, job);
            job.setPlayer(this, player);
            int randomGoalIndex = (int) (Math.random() * job.getGoals().size());
            Goal goal = job.getGoals().get(randomGoalIndex).clone();
            goal.setGoal(this, player);
            gameStatesManager.getPlayerGoals().put(player, goal);

        }
    }

    public void prepPhase(){
        onGame = true;
        gameStatesManager.setGameState(GameState.PREP);
        setNowTime(prepPhaseTime);
        // TODO:プレイヤーのテレポート
        Component message = Component.text("§a準備フェーズが開始。各自目標に備え準備せよ！");
        message.append(Component.text("\n§e" + prepPhaseTime + "秒後にゲームが開始"));
        Bukkit.getServer().broadcast(message);

        if(Bukkit.getOnlinePlayers().size() < minimumGameStartPlayers){
            Component cancelMessage = Component.text("§c参加者が最低人数(" + minimumGameStartPlayers + "人)に達していないため、ゲームを中止します。");
            Bukkit.getServer().broadcast(cancelMessage);
            resetGame();
            return;
        }

        for (Player player : gameStatesManager.getJoinedPlayers()) {
            gameStatesManager.addKillCount(player);
            playerNoticer(player);
        }
        // TODO:攻撃イベント、クリックイベントの無効化
    }

    public void startGame(){
        gameStatesManager.setGameState(GameState.IN_GAME);
        setNowTime(gamePhaseTime);
        Bukkit.getServer().broadcast(Component.text("§aゲーム開始！目標を達成せよ！"));
        for(Player player : gameStatesManager.getAlivePlayers()){
            player.setGameMode(GameMode.SURVIVAL);
            SkillScheduler.chargeUltimateSkill(player, gameStatesManager);
        }
    }

    public void endGame(){
        gameStatesManager.setGameState(GameState.END);
        setNowTime(resultPhaseTime);
        Component message = Component.text("§aゲーム終了");
        Bukkit.getServer().broadcast(message);
        List<Player> clearPlayers = new ArrayList<>();
        for(Player player : gameStatesManager.getJoinedPlayers()) {
            if(gameStatesManager.getPlayerGoals().get(player).isAchieved()){
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
                clearPlayerNames.append(gameStatesManager.getPlayerGoals().get(clearPlayers.get(i)).getName());
                if(i < clearPlayers.size() - 1){
                    clearPlayerNames.append("\n");
                }
            }
            Bukkit.getServer().broadcast(Component.text("§a目標を達成したプレイヤー\n §e" + clearPlayerNames));
        }
        for(Player player : clearPlayers){
            player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation().add(0,1,0), 100, 1,1,1, 0.1);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }
        clearScoreboardDisplay();
    }

    public void resetGame(){
        if(!onGame) return;
        scheduler.cancel();
        gameStatesManager.setGameState(GameState.WAITING);
        for(Player player : gameStatesManager.getJoinedPlayers()){
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setCustomNameVisible(true);
            player.setGameMode(GameMode.CREATIVE);
        }
        new Game();
    }

    public void playerNoticer(Player player){
        Job job = gameStatesManager.getPlayerJobs().get(player);
        Goal goal = gameStatesManager.getPlayerGoals().get(player);

        player.sendMessage("§bお前の役職は「§6" + job.getName() + "§b」、目標は「§6" + goal.getDescription() + "§b」だ。");

        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        CustomItem passive = GameItem.getItem(GameItemKeyString.PASSIVE_SKILL);
        ItemStack passiveItem = passive.getItem();
        ItemMeta pskillMeta = passiveItem.getItemMeta();
        pskillMeta.setDisplayName(job.passive_skill_name);
        List<Component> lore2 = job.passive_skill_description;
        pskillMeta.lore(lore2);
        passiveItem.setItemMeta(pskillMeta);
        inventory.addItem(passiveItem);

        CustomItem skill = GameItem.getItem(GameItemKeyString.SKILL);
        ItemStack skillItem = skill.getItem();
        ItemMeta skillMeta = skillItem.getItemMeta();
        skillMeta.setDisplayName(job.normal_skill_name);
        List<Component> lore = job.normal_skill_description;
        skillMeta.lore(lore);
        skillItem.setItemMeta(skillMeta);
        inventory.addItem(skillItem);

        if(goal.tier == Tier.TIER_3){
            CustomItem ultimateSkill = GameItem.getItem(GameItemKeyString.ULTIMATE_SKILL);
            ItemStack ultimateSkillItem = ultimateSkill.getItem();
            ItemMeta uskillMeta = ultimateSkillItem.getItemMeta();
            uskillMeta.setDisplayName(job.ultimate_skill_name);
            List<Component> lore3 = job.ultimate_skill_description;
            uskillMeta.lore(lore3);
            ultimateSkillItem.setItemMeta(uskillMeta);
            inventory.addItem(ultimateSkillItem);
        }
    }

    public void updateScoreboardDisplay(){
        Bukkit.getOnlinePlayers().forEach(this::updateScoreboardDisplay);
    }

    public void updateScoreboardDisplay(Player player){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("DokkoiGame", "dummy", Component.text("§aステータス： " + gameStatesManager.getGameState().getDisplayName()));
        objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        int i = 0;

        if(gameStatesManager.getGameState() == GameState.MATCHING){
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a参加者数: §f" + Bukkit.getOnlinePlayers().size() + "人").setScore(--i);
        }else if(gameStatesManager.getGameState() == GameState.PREP || gameStatesManager.getGameState() == GameState.IN_GAME){
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a生存者数: §f" + gameStatesManager.getAlivePlayers().size() + "人").setScore(--i);
            objective.getScore("§e目標: §f" + gameStatesManager.getPlayerGoals().get(player).getName()).setScore(--i);
            if(gameStatesManager.getPlayerGoals().get(player) instanceof GachaAddict gachaMan){
                objective.getScore("§eガチャポイント: §f" + gachaMan.getGachaPoint() + "pt").setScore(--i);
            }
        }
        player.setScoreboard(scoreboard);
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
