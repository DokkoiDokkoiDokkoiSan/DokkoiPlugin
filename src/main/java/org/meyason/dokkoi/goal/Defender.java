package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.exception.NoDefenderTargetPlayerException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goalitem.BuriBuriGuard;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Defender extends Goal {

    private BuriBuriGuard buriBuriGuard;

    private Player targetPlayer;

    public Defender(){
        super("§6Defender", "あるプレイヤーを守り抜け！");
    }

    public Player getTargetPlayer(){
        return this.targetPlayer;
    }

    @Override
    public void setGoal(Game game, Player player){
        this.game = game;
        this.player = player;
        this.targetPlayer = player;

        this.tier = Tier.TIER_1;
        setDamageMultiplier(this.tier.getDamageMultiplier());
    }

    @Override
    public void addItem() {
        setTargetPlayer();
        CustomItem item = GameItem.getItem(BuriBuriGuard.id);
        this.buriBuriGuard = (BuriBuriGuard) item;
        this.buriBuriGuard.setPlayer(game, player);
        ItemStack itemStack = buriBuriGuard.getItem();
        if(itemStack == null){
            this.player.sendMessage("§4エラーが発生しました．管理者に連絡してください：ブリブリガード取得失敗");
            return;
        }
        player.getInventory().addItem(itemStack);
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e護衛対象と自分以外の生存者"));
        this.player.sendMessage(Component.text("§bこれ以外を殺害するとペナルティが付与される"));
        return;
    }

    public void setTargetPlayer(){
        //プレイヤー抽選
        List<UUID> playerUUID = game.getGameStatesManager().getAlivePlayers();
        List<UUID> copyPlayers = new java.util.ArrayList<>(List.copyOf(playerUUID));
        copyPlayers.remove(this.player.getUniqueId());
        UUID targetUUID = copyPlayers.get(new Random().nextInt(playerUUID.size()-1));
        Player target = Bukkit.getPlayer(targetUUID);
        if(target != null){
            this.targetPlayer = target;
            this.player.sendMessage("§2生存者を §6" + targetPlayer.getName() + " §2と自分の二人だけにせよ！");
            return;
        }
        throw new NoDefenderTargetPlayerException("Defenderのターゲットプレイヤーの設定に失敗しました。");
    }

    @Override
    public boolean isAchieved() {
        List<UUID> alivePlayerUUID = this.game.getGameStatesManager().getAlivePlayers();
        if(alivePlayerUUID.size() > 2){
            this.player.sendMessage(Component.text("§c他にも生存者がいる。"));
            return false;
        }
        if(!alivePlayerUUID.contains(this.player.getUniqueId())){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        int count = 0;
        for(UUID uuid : alivePlayerUUID) {
            if(uuid.equals(this.player.getUniqueId()) || uuid.equals(this.targetPlayer.getUniqueId())) {
                count++;
            }
        }
        if(count == 2){
            this.player.sendMessage("§6やっと...二人きりになれたね。");
            this.targetPlayer.sendMessage("§6やっと...二人きりになれたね。");
            return true;
        }
        if(!alivePlayerUUID.contains(this.targetPlayer.getUniqueId())){
            this.player.sendMessage(Component.text(this.targetPlayer.getName() + "を守り抜けなかった。"));
            return false;
        }

        this.player.sendMessage("多分つぶしたと思うけどもしこのメッセージが出てきたら状況を運営に報告してください．");
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer) {
        if(targetPlayer.equals(this.player) || targetPlayer.equals(this.targetPlayer)){
            return false;
        }
        return true;
    }

}
