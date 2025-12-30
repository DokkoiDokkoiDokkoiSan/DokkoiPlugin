package org.meyason.dokkoi.game;

import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import org.bukkit.util.Vector;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.*;
import org.meyason.dokkoi.entity.GameEntityManager;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.goal.*;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goalitem.GoalMemo;
import org.meyason.dokkoi.item.jobitem.Passive;
import org.meyason.dokkoi.item.jobitem.Skill;
import org.meyason.dokkoi.item.jobitem.Ultimate;
import org.meyason.dokkoi.item.matching.JoinQueueItem;
import org.meyason.dokkoi.item.matching.QuitQueueItem;
import org.meyason.dokkoi.item.utilitem.Monei;
import org.meyason.dokkoi.job.*;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenu;
import org.meyason.dokkoi.menu.goalselectmenu.GoalSelectMenuItem;
import org.meyason.dokkoi.network.PacketProcess;
import org.meyason.dokkoi.network.PacketSender;
import org.meyason.dokkoi.scheduler.DamageableScheduler;
import org.meyason.dokkoi.scheduler.Scheduler;
import org.meyason.dokkoi.scheduler.SkillScheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class Game {

    private static Game instance;

    private GameStatesManager gameStatesManager;

    private BukkitTask scheduler;

    public final int minimumGameStartPlayers = 2;

    public final int maximumGamePlayers = JobList.getAllJobs().size();

    private final Queue<UUID> matchQueue = new LinkedList<>();
    public void addToMatchQueue(Player player){
        if(matchQueue.contains(player.getUniqueId())){
            player.sendMessage(Component.text("§c既にマッチングキューに参加しています。"));
            return;
        }
        matchQueue.add(player.getUniqueId());
        player.sendMessage(Component.text("§aマッチングキューに参加しました。"));
        updateScoreboardDisplay();
    }
    public void removeFromMatchQueue(Player player){
        if(!matchQueue.contains(player.getUniqueId())){
            player.sendMessage(Component.text("§cマッチングキューに参加していません。"));
            return;
        }
        matchQueue.remove(player.getUniqueId());
        player.sendMessage(Component.text("§cマッチングキューから退出しました。"));
        updateScoreboardDisplay();
    }
    public int getMatchQueueSize(){ return matchQueue.size(); }

    private Vector heliLocation;
    public Vector getHeliLocation() { return heliLocation; }

    private int nowTime;
    public final int matchingPhaseTime = 30;
    public final int prepPhaseTime = 60;
    public final int gamePhaseTime = 600;
    public final int preEndPhaseTime = 10;
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

    private GameLocation gameLocation;

    private LPManager lpManager;
    public LPManager getLPManager(){ return lpManager; }

    public Game(){
        instance = this;
        this.gameStatesManager = new GameStatesManager(this);
        this.gameEntityManager = new GameEntityManager();
        this.lpManager = Dokkoi.getInstance().getLPManager();
        init();
    }

    public void init(){
        scheduler = new Scheduler().runTaskTimer(Dokkoi.getInstance(), 0L, 20L);
        updateScoreboardDisplay();
        gameLocation = new GameLocation();
        gameStatesManager.init();
        gameStatesManager.setGameState(GameState.WAITING);
        setNowTime(matchingPhaseTime);
        matchQueue.clear();
        gameLocation.revertAllHeliPort();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(Dokkoi.getInstance().isEditModePlayer(player.getUniqueId())){
                continue;
            }
            CustomItem joinItem;
            CustomItem quitItem;
            try{
                joinItem = GameItem.getItem(JoinQueueItem.id);
                quitItem = GameItem.getItem(QuitQueueItem.id);
            } catch (NoGameItemException e) {
                player.sendMessage("§4エラーが発生しました．管理者に連絡してください：マッチング参加/退出アイテム取得失敗");
                return;
            }
            ItemStack joinItemStack = joinItem.getItem();
            ItemStack quitItemStack = quitItem.getItem();
            player.getInventory().addItem(joinItemStack);
            player.getInventory().addItem(quitItemStack);

            Vector lobby = gameLocation.LobbyLocation;
            player.teleport(new Location(Bukkit.getWorld("world"), lobby.getX(), lobby.getY(), lobby.getZ()));
        }
    }

    public void matching(){
        Component message = Component.text("§aマッチングを開始。" + matchingPhaseTime + "秒後に目標が決定する。");
        Bukkit.getServer().broadcast(message);

        gameStatesManager.setGameState(GameState.MATCHING);

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
        heliLocation = new Vector();
        heliLocation = gameLocation.cloneHeli();
        for(UUID uuid : matchQueue){
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            gameStatesManager.addAlivePlayer(uuid);
            gameStatesManager.addJoinedPlayer(uuid);
            player.getInventory().clear();
            player.getInventory().setHelmet(null);
            player.setMaxHealth(40.0);
            player.setHealth(40.0);
            player.setFoodLevel(20);
            player.setCustomNameVisible(false);
        }
        for(Player player : Bukkit.getOnlinePlayers()){
            if(Dokkoi.getInstance().isEditModePlayer(player.getUniqueId())){
                continue;
            }
            if(!matchQueue.contains(player.getUniqueId())){
                player.setGameMode(GameMode.SPECTATOR);
            }
        }

        onGame = true;
        gameStatesManager.setGameState(GameState.PREP);
        setNowTime(prepPhaseTime);

        Component message = Component.text("§a準備フェーズが開始。各自準備せよ！");
        message.append(Component.text("\n§e" + prepPhaseTime + "秒後にゲームが開始"));
        Bukkit.getServer().broadcast(message);

        if(gameStatesManager.getJoinedPlayers().size() < minimumGameStartPlayers){
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

            PacketContainer pk = PacketProcess.hideNameTag(player, null);
            gameStatesManager.getJoinedPlayers().forEach(other -> {
                if(!other.equals(uuid)){
                    Player sender = Bukkit.getPlayer(other);
                    if(sender != null && sender.isOnline()){
                        PacketSender.sendPacket(sender, pk);
                    }
                }
            });

            GoalSelectMenu goalSelectMenu = new GoalSelectMenu();
            goalSelectMenu.sendMenu(player);


            CustomItem item;
            try {
                item = GameItem.getItem(GoalSelectMenuItem.id);
            } catch (NoGameItemException e) {
                player.sendMessage("§4エラーが発生しました．管理者に連絡してください：目標選択メニューアイテム取得失敗");
                e.printStackTrace();
                continue;
            }
            ItemStack itemStack = item.getItem();
            player.getInventory().addItem(itemStack);
        }
    }

    public void startGame(){
        int tier1Count = 0;
        int tier2Count = 0;
        int tier3Count = 0;
        List<Vector> availableSpawnLocations = new ArrayList<>(gameLocation.respawnLocations);

        for (UUID uuid : gameStatesManager.getJoinedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            gameStatesManager.setIsEnableAttack(uuid, true);
            int randomIndex = (int) (Math.random() * availableSpawnLocations.size());
            Vector spawnLocation = availableSpawnLocations.get(randomIndex);
            availableSpawnLocations.remove(randomIndex);
            player.teleport(new Location(Bukkit.getWorld("world"), spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ()));
            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_0, 1.0F, 1.0F);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            player.getInventory().clear();

            gameStatesManager.addKillCount(uuid);
            gameStatesManager.addAdditionalDamage(uuid, 0);
            gameStatesManager.setDamageCutPercent(uuid, 0);
            gameStatesManager.addIsDeactivateDamageOnce(uuid, false);

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
        for(Player player : Bukkit.getOnlinePlayers()){
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false, false));
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
            player.setGameMode(GameMode.ADVENTURE);
            gameStatesManager.getPlayerJobs().get(uuid).chargeUltimateSkill(player, gameStatesManager);
            updateScoreboardDisplay(player);

            SkillScheduler scheduler = new SkillScheduler(this, player);
            scheduler.runTaskTimer(Dokkoi.getInstance(), 0L, 20L);
            gameStatesManager.addCoolDownScheduler(uuid, scheduler);

            DamageableScheduler damageableScheduler = new DamageableScheduler(this, player);
            damageableScheduler.runTaskTimer(Dokkoi.getInstance(), 0L, 10L);
            gameStatesManager.setDamageableTasks(uuid, damageableScheduler);

            Job job = gameStatesManager.getPlayerJobs().get(uuid);
            job.ready();

            player.setCustomNameVisible(false);
        }

        ChestProvider.getInstance().startTask();
    }

    public void preEndGame(){
        gameStatesManager.setGameState(GameState.PRE_END);
        Component message = Component.text("§bゲーム終了。10秒後に結果発表が行われる。");
        Bukkit.getServer().broadcast(message);
        setNowTime(preEndPhaseTime);
        for(UUID uuid : gameStatesManager.getJoinedPlayers()){
            if(gameStatesManager.isNaito(uuid)){
                Player player = Bukkit.getPlayer(uuid);
                if(player == null || !player.isOnline()){
                    continue;
                }
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    public void endGame(){
        gameStatesManager.setGameState(GameState.END);
        gameEntityManager.unregisterEntity();
        setNowTime(resultPhaseTime);
        Component message = Component.text("§a結果発表おおおおおおおおおおおおおおおおおお！");
        Bukkit.getServer().broadcast(message);

        ChestProvider.getInstance().cancelTask();

        for (UUID uuid : gameStatesManager.getJoinedPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null || !player.isOnline()){
                continue;
            }
            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_5, 1.0F, 1.0F);
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

            UUID uuid = player.getUniqueId();
            long reward = 20L;
            if(gameStatesManager.getPlayerGoals().get(uuid).tier == Tier.TIER_2){
                reward = 75L;
            }else if(gameStatesManager.getPlayerGoals().get(uuid).tier == Tier.TIER_1){
                reward = 150L;
            }

            player.sendMessage("§eあなたは勝利報酬として§6" + reward + "LP§eを獲得しました！");

            NamespacedKey key = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
            int moneyCount = 0;

            for(ItemStack stack : player.getInventory().getContents()){
                if(stack == null) continue;
                ItemMeta meta = stack.getItemMeta();
                if(meta == null) continue;
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if(!container.has(key)) {
                    continue;
                }
                String itemName = container.get(key, PersistentDataType.STRING);
                if(itemName != null && itemName.equals(Monei.id)){
                    moneyCount += stack.getAmount();
                    stack.setAmount(0);
                }
            }
            if(moneyCount > 0){
                long moneyReward = moneyCount * 5L;
                player.sendMessage("§e所持していた§6モネイ×" + moneyCount + "§eを換金し、追加で§6" + moneyReward + "LP§eを獲得しました！");
                reward += moneyReward;
            }

            lpManager.addLP(uuid, reward);
        }
        clearScoreboardDisplay();
    }

    public void resetGame(){
//        if(!onGame) return;
        scheduler.cancel();
        ChestProvider.removeAllChests();
        ChestProvider.getInstance().cancelTask();
        gameStatesManager.setGameState(GameState.WAITING);
        if(!gameStatesManager.getJoinedPlayers().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(Dokkoi.getInstance().isEditModePlayer(player.getUniqueId())){
                    continue;
                }
                UUID uuid = player.getUniqueId();
                if (gameStatesManager.getCoolDownScheduler().containsKey(uuid)) {
                    gameStatesManager.getCoolDownScheduler().get(uuid).cancel();
                }
                player.getInventory().clear();
                player.getInventory().setHelmet(null);
                player.setMaxHealth(20.0);
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.setCustomNameVisible(true);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }
        matchQueue.clear();
        gameStatesManager.clearAll();
        if(heliLocation != null){
            gameLocation.revertHeliPort(heliLocation);
        }
        new Game();
    }

    public void playerGoalNoticer(UUID uuid){
        Player player = Bukkit.getPlayer(uuid);
        if(player == null || !player.isOnline()){ return; }

        Job job = gameStatesManager.getPlayerJobs().get(uuid);
        Goal goal = gameStatesManager.getPlayerGoals().get(uuid);

        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        CustomItem goalMemo;
        try{
            goalMemo = GameItem.getItem(GoalMemo.id);
        } catch (NoGameItemException e){
            player.sendMessage("§4エラーが発生しました．管理者に連絡してください：目標メモアイテム取得失敗");
            return;
        }
        ItemStack goalMemoItem = goalMemo.getItem();
        inventory.addItem(goalMemoItem);

        CustomItem passive;
        try{
            passive = GameItem.getItem(Passive.id);
        } catch (NoGameItemException e){
            player.sendMessage("§4エラーが発生しました．管理者に連絡してください：パッシブアイテム取得失敗");
            return;
        }
        ItemStack passiveItem = passive.getItem();
        ItemMeta pskillMeta = passiveItem.getItemMeta();
        pskillMeta.setDisplayName(job.passive_skill_name);
        List<Component> lore2 = job.passive_skill_description;
        pskillMeta.lore(lore2);
        passiveItem.setItemMeta(pskillMeta);
        inventory.addItem(passiveItem);

        CustomItem skill;
        try{
            skill = GameItem.getItem(Skill.id);
        } catch (NoGameItemException e){
            player.sendMessage("§4エラーが発生しました．管理者に連絡してください：スキルアイテム取得失敗");
            return;
        }
        ItemStack skillItem = skill.getItem();
        ItemMeta skillMeta = skillItem.getItemMeta();
        skillMeta.setDisplayName(job.normal_skill_name);
        List<Component> lore = job.normal_skill_description;
        skillMeta.lore(lore);
        skillItem.setItemMeta(skillMeta);
        inventory.addItem(skillItem);

        if(goal.tier == Tier.TIER_3){
            CustomItem ultimateSkill;
            try{
                ultimateSkill = GameItem.getItem(Ultimate.id);
            } catch (NoGameItemException e){
                player.sendMessage("§4エラーが発生しました．管理者に連絡してください：ULTアイテム取得失敗");
                return;
            }
            ItemStack ultimateSkillItem = ultimateSkill.getItem();
            ItemMeta uskillMeta = ultimateSkillItem.getItemMeta();
            uskillMeta.setDisplayName(job.ultimate_skill_name);
            List<Component> lore3 = job.ultimate_skill_description;
            uskillMeta.lore(lore3);
            ultimateSkillItem.setItemMeta(uskillMeta);
            inventory.addItem(ultimateSkillItem);
        }

        goal.addItem();
    }

    public void updateScoreboardDisplay(){
        Bukkit.getOnlinePlayers().forEach(this::updateScoreboardDisplay);
    }

    public void updateScoreboardDisplay(Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(player.getName(), Criteria.DUMMY, "§aステータス： " + gameStatesManager.getGameState().getDisplayName());
        objective.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);

        UUID uuid = player.getUniqueId();

        int i = 0;

        if(gameStatesManager.getGameState() == GameState.WAITING){
            objective.getScore("§b所持金: §f" + lpManager.getLP(uuid) + "LP").setScore(--i);
            objective.getScore("§a参加人数: §f" + Bukkit.getOnlinePlayers().size() + "人").setScore(--i);
            objective.getScore("§aキュー人数: §f" + getMatchQueueSize() + "人").setScore(--i);
        }else if(gameStatesManager.getGameState() == GameState.MATCHING){
            objective.getScore("§b所持金: §f" + lpManager.getLP(uuid) + "LP").setScore(--i);
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§aキュー人数: §f" + getMatchQueueSize() + "人").setScore(--i);
        }else if(gameStatesManager.getGameState() == GameState.PREP) {
            objective.getScore("§b所持金: §f" + lpManager.getLP(uuid) + "LP").setScore(--i);
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a参加人数: §f" + gameStatesManager.getJoinedPlayers().size() + "人").setScore(--i);
            if(gameStatesManager.getJoinedPlayers().contains(player.getUniqueId())) {
                objective.getScore("§e役職: §f" + gameStatesManager.getPlayerJobs().get(uuid).getName()).setScore(--i);
            }
        }else if(gameStatesManager.getGameState() == GameState.IN_GAME){
            objective.getScore("§b残り時間: §f" + getNowTime() + "秒").setScore(--i);
            objective.getScore("§a生存者数: §f" + gameStatesManager.getAlivePlayers().size() + "人").setScore(--i);
            if(gameStatesManager.getJoinedPlayers().contains(player.getUniqueId())) {
                objective.getScore("§e役職: §f" + gameStatesManager.getPlayerJobs().get(uuid).getName()).setScore(--i);
                objective.getScore("§e目標: §f" + gameStatesManager.getPlayerGoals().get(uuid).getName()).setScore(--i);
                objective.getScore("§aスキル: " + gameStatesManager.getPlayerJobs().get(uuid).getCoolTimeSkillViewer()).setScore(--i);
                objective.getScore("§aULT: " + gameStatesManager.getPlayerJobs().get(uuid).getCoolTimeSkillUltimateViewer()).setScore(--i);

                Job job = gameStatesManager.getPlayerJobs().get(uuid);
                Goal goal = gameStatesManager.getPlayerGoals().get(uuid);
                String achievedColor = "6";

                if (goal instanceof MaidenGazer maidenGazer) {
                    objective.getScore("§e視線誘導した時間: §f" + maidenGazer.getPoint() + "秒").setScore(--i);
                } else if (goal instanceof CarpetBombing carpetBombing) {
                    String color = "c";
                    if (carpetBombing.getKillCount() >= carpetBombing.goalNumber) {
                        color = achievedColor;
                    }
                    objective.getScore("§e目標人数: §f" + carpetBombing.goalNumber + "人").setScore(--i);
                    objective.getScore("§e自爆による殺害人数: §" + color + carpetBombing.getKillCount() + "人").setScore(--i);
                } else if (goal instanceof Defender defender) {
                    objective.getScore("§e護衛対象: §f" + defender.getTargetPlayer().getName()).setScore(--i);
                } else if (goal instanceof Killer || goal instanceof LastMan) {
                    objective.getScore("§e残り生存者: §f" + gameStatesManager.getAlivePlayers().size()).setScore(--i);
                } else if (goal instanceof MassTierKiller massTierKiller) {
                    objective.getScore("§eターゲットのTier: §f" + massTierKiller.getTargetTier().name()).setScore(--i);
                } else if (goal instanceof DrugEnforcementAdministration dea) {
                    Executor executor = (Executor) job;
                    String color = "c";
                    if (executor.getArrestCount() >= 3) {
                        color = achievedColor;
                    }
                    objective.getScore("§e現在の逮捕数: §" + color + executor.getArrestCount() + "人").setScore(--i);
                } else if (goal instanceof SugiYakkyoku) {
                    String color = "c";
                    DrugStore drugStore = (DrugStore) job;
                    if (drugStore.getSellCount() >= 10) {
                        color = achievedColor;
                    }
                    objective.getScore("§e現在の販売数: §" + color + drugStore.getSellCount() + "個").setScore(--i);
                } else if (goal instanceof MatsumotoKiyoshi) {
                    String color = "c";
                    DrugStore drugStore = (DrugStore) job;
                    if (drugStore.getPickCount() >= 3) {
                        color = achievedColor;
                    }
                    objective.getScore("§e現在の回数: §" + color + drugStore.getPickCount() + "回").setScore(--i);
                } else if (goal instanceof FiftyPercent) {
                    objective.getScore("§a参加者数: §f" + gameStatesManager.getJoinedPlayers().size() + "人").setScore(--i);
                } else if (goal instanceof SkeletonSlayer skeletonSlayer) {
                    String color = "c";
                    if (skeletonSlayer.getSkeletonsKilled() >= 50) {
                        color = achievedColor;
                    }
                    objective.getScore("§e目標殺害数: §f" + 50 + "体").setScore(--i);
                    objective.getScore("§e現在の殺害数: §" + color + skeletonSlayer.getSkeletonsKilled() + "体").setScore(--i);
                }

                if (job instanceof Explorer explorer) {
                    if (goal instanceof KetsumouHunter ketsumouHunter) {
                        String color = "c";
                        if (ketsumouHunter.getTargetKetsumouCount() <= explorer.getHaveKetsumouCount()) {
                            color = achievedColor;
                        }
                        objective.getScore("§e目標の§9§lけつ毛§r§e: §" + color + ketsumouHunter.getTargetKetsumouCount() + "個").setScore(--i);
                    }
                    objective.getScore("§e発見した§9§lけつ毛§r§e: §f" + explorer.getHaveKetsumouCount() + "個").setScore(--i);
                } else if (job instanceof Prayer prayer) {
                    objective.getScore("§eガチャポイント: §f" + prayer.getGachaPoint()).setScore(--i);
                    objective.getScore("§eガチャ回数: §f" + prayer.getGachaCount() + "回").setScore(--i);
                } else if (job instanceof Summoner) {
                    objective.getScore("§e召喚体数: §f" + gameStatesManager.getNaito().size() + "体").setScore(--i);
                } else if (job instanceof Photographer photographer) {
                    objective.getScore("§e撮影人数: §f" + photographer.getTakenPhotoPlayersCount() + "人").setScore(--i);
                }
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
