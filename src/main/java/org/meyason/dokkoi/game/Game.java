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
import org.bukkit.scoreboard.*;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.*;
import org.meyason.dokkoi.entity.GameEntityManager;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.goal.*;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.jobitem.Passive;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.item.jobitem.Ultimate;
import org.meyason.dokkoi.job.Executor;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.job.Prayer;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenu;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenuItem;
import org.meyason.dokkoi.scheduler.Scheduler;
import org.meyason.dokkoi.scheduler.SkillScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {

    private static Game instance;

    private GameStatesManager gameStatesManager;

    private BukkitTask scheduler;

    public final int minimumGameStartPlayers = 2;

    private int nowTime;
    public final int matchingPhaseTime = 5;
    public final int prepPhaseTime = 60;
    public final int gamePhaseTime = 600;
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

    private GameEntityManager gameEntityManager;

    public Game(){
        instance = this;
        this.gameStatesManager = new GameStatesManager(this);
        this.gameEntityManager = new GameEntityManager();
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

        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam("nametag");
            if(team == null) team = scoreboard.registerNewTeam("nametag");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            team.addEntry(player.getName());
            player.setScoreboard(scoreboard);
        }

        updateScoreboardDisplay();
    }

    public void prepPhase(){
        for(Player player : Bukkit.getOnlinePlayers()){
            UUID uuid = player.getUniqueId();
            gameStatesManager.addAlivePlayer(uuid);
            gameStatesManager.addJoinedPlayer(uuid);
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.setMaxHealth(40.0);
            player.setHealth(40.0);
            player.setFoodLevel(20);
            player.setCustomNameVisible(false);
        }
        onGame = true;
        gameStatesManager.setGameState(GameState.PREP);
        setNowTime(prepPhaseTime);
        // TODO:プレイヤーのテレポート
        Component message = Component.text("§a準備フェーズが開始。各自準備せよ！");
        message.append(Component.text("\n§e" + prepPhaseTime + "秒後にゲームが開始"));
        Bukkit.getServer().broadcast(message);

        if(Bukkit.getOnlinePlayers().size() < minimumGameStartPlayers){
            Component cancelMessage = Component.text("§c参加者が最低人数(" + minimumGameStartPlayers + "人)に達していないため、ゲームを中止します。");
            Bukkit.getServer().broadcast(cancelMessage);
            resetGame();
            return;
        }

        List<Job> jobList = new ArrayList<>(JobList.getAllJobs());

        // jobの割り当て
        for(UUID uuid : gameStatesManager.getJoinedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            int randomIndex = (int) (Math.random() * jobList.size());
            Job job = jobList.get(randomIndex).clone();
            jobList.remove(randomIndex);
            job.setPlayer(this, player);
            gameStatesManager.getPlayerJobs().put(uuid, job);
            player.sendMessage("§bお前の役職は「§6" + job.getName() + "§b」だ。");
            player.sendMessage("§b勝利条件を選択せよ。");
        }

        for(UUID uuid : gameStatesManager.getJoinedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            GoalSelectMenu goalSelectMenu = new GoalSelectMenu();
            goalSelectMenu.sendMenu(player);
            CustomItem item = null;
            try {
                item = GameItem.getItem(GoalSelectMenuItem.id);
            } catch (NoGameItemException e) {
                player.sendMessage("§4エラーが発生しました．管理者に連絡してください：目標選択メニューアイテム取得失敗");
                e.printStackTrace();
                return;
            }
            ItemStack itemStack = item.getItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemStack.setItemMeta(itemMeta);
            player.getInventory().setItem(1, itemStack);
        }
    }

    public void startGame(){
        int tier1Count = 0;
        int tier2Count = 0;
        int tier3Count = 0;
        for (UUID uuid : gameStatesManager.getJoinedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            player.getInventory().clear();
            gameStatesManager.addKillCount(uuid);
            gameStatesManager.addAdditionalDamage(uuid, 0);
            gameStatesManager.addDamageCutPercent(uuid, 0);
            gameStatesManager.addIsDeactivateDamageOnce(uuid, false);
            gameStatesManager.addMoneyMap(uuid, 5L);
            Goal goal = gameStatesManager.getPlayerGoals().get(uuid);
            if(goal == null){
                Job job = gameStatesManager.getPlayerJobs().get(uuid);
                int randomGoalIndex = (int) (Math.random() * job.getGoals().size());
                goal = job.getGoals().get(randomGoalIndex).clone();
                goal.setGoal(this, player);
                gameStatesManager.getPlayerGoals().put(uuid, goal);
                job.attachGoal(goal);
            }
            if(goal.tier == Tier.TIER_1){
                tier1Count++;
            }else if(goal.tier == Tier.TIER_2){
                tier2Count++;
            }else if(goal.tier == Tier.TIER_3) {
                tier3Count++;
            }
            playerGoalNoticer(uuid);
        }
        List<Component> goalInstructions = List.of(
                Component.text("§a---本ゲームの勝利条件内訳---"),
                Component.text("§6TIER 1 : " + tier1Count + "人"),
                Component.text("§bTIER 2 : " + tier2Count + "人"),
                Component.text("§cTIER 3 : " + tier3Count + "人")
        );
        for(Component line : goalInstructions){
            Bukkit.getServer().broadcast(line);
        }

        gameEntityManager.registerEntity();
        gameStatesManager.setGameState(GameState.IN_GAME);
        setNowTime(gamePhaseTime);
        Bukkit.getServer().broadcast(Component.text("§aゲーム開始！各自勝利条件を達成せよ！"));
        Bukkit.getServer().broadcast(Component.text("§e試合開始から100秒経過するまで、攻撃は無効化される。"));

        for(UUID uuid : gameStatesManager.getAlivePlayers()){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            player.closeInventory();
            player.setGameMode(GameMode.SURVIVAL);
            gameStatesManager.getPlayerJobs().get(uuid).chargeUltimateSkill(player, gameStatesManager);
            updateScoreboardDisplay(player);

            SkillScheduler scheduler = new SkillScheduler(this, player);
            scheduler.runTaskTimer(Dokkoi.getInstance(), 0L, 20L);
            gameStatesManager.addCoolDownScheduler(uuid, scheduler);

            Job job = gameStatesManager.getPlayerJobs().get(uuid);
            job.ready();

            player.setCustomNameVisible(false);
        }
    }

    public void endGame(){
        gameStatesManager.setGameState(GameState.END);
        gameEntityManager.unregisterEntity();
        setNowTime(resultPhaseTime);
        Component message = Component.text("§aゲーム終了");
        Bukkit.getServer().broadcast(message);

        for (UUID uuid : gameStatesManager.getJoinedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam("nametag");
            if(team == null) team = scoreboard.registerNewTeam("nametag");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.addEntry(player.getName());
            player.setScoreboard(scoreboard);
        }

        List<Player> clearPlayers = new ArrayList<>();
        for(UUID uuid : gameStatesManager.getJoinedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){ continue; }
            //全部のポーション効果消す
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            if(gameStatesManager.getPlayerGoals().get(uuid).isAchieved(true)){
                player.sendMessage("§6お前は目標を達成した！");
                clearPlayers.add(player);
            }else{
                player.sendMessage("§cお前は目標を達成できなかった...");
            }
            player.setCustomNameVisible(true);
        }

        if(clearPlayers.isEmpty()){
            Bukkit.getServer().broadcast(Component.text("§c誰も目標を達成できなかった..."));
        }else{
            StringBuilder clearPlayerNames = new StringBuilder();
            for(int i = 0; i < clearPlayers.size(); i++){
                clearPlayerNames.append("§e");
                clearPlayerNames.append(clearPlayers.get(i).getName());
                clearPlayerNames.append(": ");
                clearPlayerNames.append(gameStatesManager.getPlayerGoals().get(clearPlayers.get(i).getUniqueId()).getName());
                if(i < clearPlayers.size() - 1){
                    clearPlayerNames.append("\n");
                }
            }
            Bukkit.getServer().broadcast(Component.text("§a目標を達成したプレイヤー\n" + clearPlayerNames));
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
        for(UUID uuid : gameStatesManager.getJoinedPlayers()){
            if(gameStatesManager.getCoolDownScheduler().containsKey(uuid)){
                gameStatesManager.getCoolDownScheduler().get(uuid).cancel();
            }
        }
        gameStatesManager.setGameState(GameState.WAITING);
        for(UUID uuid : gameStatesManager.getJoinedPlayers()){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setCustomNameVisible(true);
            player.setGameMode(GameMode.CREATIVE);
        }
        gameStatesManager.clearAll();
        new Game();
    }

    public void playerGoalNoticer(UUID uuid){
        Player player = Bukkit.getPlayer(uuid);
        if(player == null || !player.isOnline()){ return; }

        Job job = gameStatesManager.getPlayerJobs().get(uuid);
        Goal goal = gameStatesManager.getPlayerGoals().get(uuid);

        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        try {
            CustomItem passive = GameItem.getItem(Passive.id);
            ItemStack passiveItem = passive.getItem();
            ItemMeta pskillMeta = passiveItem.getItemMeta();
            pskillMeta.setDisplayName(job.passive_skill_name);
            List<Component> lore2 = job.passive_skill_description;
            pskillMeta.lore(lore2);
            passiveItem.setItemMeta(pskillMeta);
            inventory.addItem(passiveItem);

            CustomItem skill = GameItem.getItem(Skill.id);
            ItemStack skillItem = skill.getItem();
            ItemMeta skillMeta = skillItem.getItemMeta();
            skillMeta.setDisplayName(job.normal_skill_name);
            List<Component> lore = job.normal_skill_description;
            skillMeta.lore(lore);
            skillItem.setItemMeta(skillMeta);
            inventory.addItem(skillItem);
        } catch (NoGameItemException e) {
            player.sendMessage("§4エラーが発生しました．管理者に連絡してください：スキルアイテム取得失敗");
            e.printStackTrace();
            return;
        }

        if(goal.tier == Tier.TIER_3){
            try {
                CustomItem ultimateSkill = GameItem.getItem(Ultimate.id);
                ItemStack ultimateSkillItem = ultimateSkill.getItem();
                ItemMeta uskillMeta = ultimateSkillItem.getItemMeta();
                uskillMeta.setDisplayName(job.ultimate_skill_name);
                List<Component> lore3 = job.ultimate_skill_description;
                uskillMeta.lore(lore3);
                ultimateSkillItem.setItemMeta(uskillMeta);
                inventory.addItem(ultimateSkillItem);
            } catch (NoGameItemException e) {
                player.sendMessage("§4エラーが発生しました．管理者に連絡してください：ULTスキルアイテム取得失敗");
                e.printStackTrace();
                return;
            }
        }

        goal.addItem();
    }

    public void updateScoreboardDisplay(){
        Bukkit.getOnlinePlayers().forEach(this::updateScoreboardDisplay);
    }

    public void updateScoreboardDisplay(Player player){
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(player.getName(), Criteria.DUMMY, "§aステータス： " + gameStatesManager.getGameState().getDisplayName());
        objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        UUID uuid = player.getUniqueId();

        int i = 0;

        if(gameStatesManager.getGameState() == GameState.MATCHING){
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a参加人数: §f" + Bukkit.getOnlinePlayers().size() + "人").setScore(--i);
        }else if(gameStatesManager.getGameState() == GameState.PREP) {
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a参加人数: §f" + gameStatesManager.getJoinedPlayers().size() + "人").setScore(--i);
            objective.getScore("§e役職: §f" + gameStatesManager.getPlayerJobs().get(uuid).getName()).setScore(--i);
        }else if(gameStatesManager.getGameState() == GameState.IN_GAME){
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a生存者数: §f" + gameStatesManager.getAlivePlayers().size() + "人").setScore(--i);
            objective.getScore("§d所持モネイ: §f" + gameStatesManager.getMoneyMap().get(uuid)).setScore(--i);
            objective.getScore("§e役職: §f" + gameStatesManager.getPlayerJobs().get(uuid).getName()).setScore(--i);
            objective.getScore("§e目標: §f" + gameStatesManager.getPlayerGoals().get(uuid).getName()).setScore(--i);
            objective.getScore("§aスキル: " + gameStatesManager.getPlayerJobs().get(uuid).getCoolTimeSkillViewer()).setScore(--i);
            objective.getScore("§aULT: " + gameStatesManager.getPlayerJobs().get(uuid).getCoolTimeSkillUltimateViewer()).setScore(--i);

            Job job = gameStatesManager.getPlayerJobs().get(uuid);
            Goal goal = gameStatesManager.getPlayerGoals().get(uuid);
            String achievedColor = "6";
            if(goal instanceof MaidenGazer maidenGazer){
                objective.getScore("§e視線誘導した時間: §f" + maidenGazer.getPoint() + "秒").setScore(--i);
            }else if(goal instanceof CarpetBombing carpetBombing){
                String color = "c";
                if(carpetBombing.getKillCount() >= carpetBombing.goalNumber){
                    color = achievedColor;
                }
                objective.getScore("§e目標人数: §f" + carpetBombing.goalNumber + "人").setScore(--i);
                objective.getScore("§e自爆による殺害人数: §" + color + carpetBombing.getKillCount() + "人").setScore(--i);
            }else if(goal instanceof Defender defender){
                objective.getScore("§e護衛対象: §f" + defender.getTargetPlayer().getName()).setScore(--i);
            }else if(goal instanceof Killer || goal instanceof LastMan){
                objective.getScore("§e残り生存者: §f" + gameStatesManager.getAlivePlayers().size()).setScore(--i);
            }else if(goal instanceof MassTierKiller massTierKiller){
                objective.getScore("§eターゲットのTier: §f" + massTierKiller.getTargetTier().name()).setScore(--i);
            }else if(goal instanceof DrugEnforcementAdministration dea){
                Executor executor = (Executor) job;
                String color = "c";
                if(executor.getArrestCount() >= 3){
                    color = achievedColor;
                }
                objective.getScore("§e目標逮捕数: §f" +  3 + "人").setScore(--i);
                objective.getScore("§e現在の逮捕数: §" + color + executor.getArrestCount() + "人").setScore(--i);
            }

            if(job instanceof Explorer explorer){
                if(goal instanceof KetsumouHunter ketsumouHunter){
                    String color = "c";
                    if(ketsumouHunter.getTargetKetsumouCount() <= explorer.getHaveKetsumouCount()) {
                        color = achievedColor;
                    }
                    objective.getScore("§e目標の§9§lけつ毛§r§e: §" + color + ketsumouHunter.getTargetKetsumouCount() + "個").setScore(--i);
                }else if(goal instanceof KetsumouPirate){
                    int targetNum = 9;
                    String color = "c";
                    if(targetNum <= explorer.getHaveKetsumouCount()) {
                        color = achievedColor;
                    }
                    objective.getScore("§e目標の§9§lけつ毛§r§e: §" + color + targetNum + "個").setScore(--i);
                }
                objective.getScore("§e発見した§9§lけつ毛§r§e: §f" + explorer.getHaveKetsumouCount() + "個").setScore(--i);
            }else if(job instanceof Prayer prayer){
                objective.getScore("§eガチャポイント: §f" + prayer.getGachaPoint()).setScore(--i);
                objective.getScore("§eガチャ回数: §f" + prayer.getGachaCount() + "回").setScore(--i);
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

    public boolean checkAllPlayerGoalAchieved(){
        for(UUID uuid : gameStatesManager.getAlivePlayers()){
            Goal goal = gameStatesManager.getPlayerGoals().get(uuid);
            if(!goal.isAchieved(false)){
                return false;
            }
        }
        return true;
    }

}
