package org.meyason.dokkoi.scheduler;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.exception.GameStateException;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.utilitem.Monei;
import org.meyason.dokkoi.job.Lonely;

import java.util.UUID;

public class Scheduler extends BukkitRunnable {

    private boolean onCountdown = false;

    public void run() {
        Game game = Game.getInstance();
        if (game.getGameStatesManager().getGameState() == null) {
            throw new GameStateException("Game state is not set.");
        }

        switch (game.getGameStatesManager().getGameState()) {
            case WAITING:
                if(game.getMatchQueueSize() >= game.minimumGameStartPlayers){
                    Bukkit.getServer().broadcast(Component.text("§a参加者が最低人数に達したため、マッチングフェーズに移行します。"));
                    game.matching();
                }
                game.updateScoreboardDisplay();
                break;
            case MATCHING:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getMatchQueueSize() < game.minimumGameStartPlayers){
                    Bukkit.getServer().broadcast(Component.text("§c参加者が最低人数を下回ったため、待機フェーズに戻ります。"));
                    game.resetGame();
                    return;
                }
                if(game.getMatchQueueSize() == game.maximumGamePlayers){
                    Bukkit.getServer().broadcast(Component.text("§aマッチング完了。準備フェーズに移行します。"));
                    game.prepPhase();
                    return;
                }
                if(game.getNowTime() < 0){
                    Bukkit.getServer().broadcast(Component.text("§aマッチング完了。準備フェーズに移行します。"));
                    game.prepPhase();
                }
                game.updateScoreboardDisplay();
                break;
            case PREP:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    game.startGame();
                }
                boolean goalFlag = false;
                for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
                    Player player = Bukkit.getPlayer(uuid);
                    if(player == null){
                        continue;
                    }
                    if(!game.getGameStatesManager().getPlayerGoals().containsKey(uuid)){
                        goalFlag = true;
                        continue;
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 1));
                }
                if(game.getNowTime() <= 5){
                    for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
                        Player player = Bukkit.getPlayer(uuid);
                        if(player == null){
                            continue;
                        }
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.0f);
                    }
                    Bukkit.getServer().broadcast(Component.text(game.getNowTime()));
                }
                if(!goalFlag){
                    Bukkit.getServer().broadcast(Component.text("§a全員が目標を設定しました。ゲームを開始します。"));
                    game.startGame();
                }
                game.updateScoreboardDisplay();
                break;
            case IN_GAME:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    onCountdown = false;
                    game.preEndGame();
                }
                if(game.getGameStatesManager().getAlivePlayers().size() <= 1){
                    onCountdown = false;
                    game.preEndGame();
                }

                if(!onCountdown && Game.getInstance().checkAllPlayerGoalAchieved()){
                    Bukkit.getServer().broadcast(Component.text("§6生存している全てのプレイヤーが目標を達成しました。30秒間この状態が維持されればゲームを終了します。"));
                    GoalAchieveWatchDog();
                }

                if(game.getNowTime() % 100 == 0){
                    if(game.getNowTime() == 500){
                        Bukkit.getServer().broadcast(Component.text("§e試合開始から100秒経過しました。保護システムが無効化されました。"));
                    }
                    for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
                        Player player = Bukkit.getPlayer(uuid);
                        if(player == null || player.getGameMode().equals(GameMode.SPECTATOR)){
                            continue;
                        }
                        if(game.getGameStatesManager().getPlayerJobs().get(player.getUniqueId()) instanceof Lonely lonely){
                            if(lonely.isUltimateActive){
                                lonely.setUltimateActive(false);
                                continue;
                            }
                        }
                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 1));

                        CustomItem moneiItem;
                        try{
                            moneiItem = GameItem.getItem(Monei.id);
                        } catch (NoGameItemException e){
                            player.sendMessage(Component.text("§4エラーが発生しました。運営に報告してください。：Monei取得失敗"));
                            continue;
                        }
                        ItemStack item = moneiItem.getItem();
                        item.setAmount(5);
                        if(player.getInventory().firstEmpty() == -1) {
                            player.sendMessage("§e空から§6モネイ×5§eが落ちてきた！");
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }else{
                            player.getInventory().addItem(item);
                            player.sendMessage("§6生存ボーナスとして、モネイ×5§eを受け取りました。");
                        }
                    }
                }
//                if(game.getNowTime() > 0 && game.getNowTime() <= 500 && game.getNowTime() % 50 == 0){
//                    SkeletonSpawn.spawnSkeletons();
//                }
                game.updateScoreboardDisplay();
                break;

            case PRE_END:
                onCountdown = false;
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    game.endGame();
                }
                game.updateScoreboardDisplay();
                break;
            case END:
                game.setNowTime(game.getNowTime() - 1);
                if(game.getNowTime() < 0){
                    game.resetGame();
                }
                game.updateScoreboardDisplay();
                break;
            default:
                // その他の状態の処理
                break;
        }
        return;
    }

    private void GoalAchieveWatchDog(){
        onCountdown = true;
        BukkitRunnable goalAchieveTask = new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                Game game = Game.getInstance();
                if(!onCountdown){
                    this.cancel();
                    return;
                }
                if(!game.checkAllPlayerGoalAchieved()){
                    Bukkit.getServer().broadcast(Component.text("§c全員が目標を達成した状態ではなくなったので、カウントダウンがキャンセルされました。"));
                    this.cancel();
                    onCountdown = false;
                    return;
                }
                if(counter >= 30){
                    Bukkit.getServer().broadcast(Component.text("§6全員が目標を達成した状態が30秒間維持されたため、ゲームを終了します。"));
                    this.cancel();
                    game.preEndGame();
                    return;
                }
                counter++;
            }
        };

        goalAchieveTask.runTaskTimer(Dokkoi.getInstance(), 0L, 20L);
    }
}
