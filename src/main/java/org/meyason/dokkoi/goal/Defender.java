package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goal.BuriBuriGuard;
import org.meyason.dokkoi.item.goal.KillerList;

import java.util.List;
import java.util.Random;

public class Defender extends Goal {

    private BuriBuriGuard buriBuriGuard;

    private Player targetPlayer;

    public Defender(){
        super("Defender", "あるプレイヤーを守り抜け！");
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
        //プレイヤー抽選
        List<Player> players = game.getGameStatesManager().getAlivePlayers();
        players.remove(this.player);
        Player target = players.get(new Random().nextInt(players.size()));
        if(target != null){
            this.targetPlayer = target;
        }

        this.player.sendMessage("§2生存者を §6" + targetPlayer.getName() + " §2と自分の二人だけにせよ！");
        CustomItem item = GameItem.getItem(BuriBuriGuard.id);
        if(item == null){
            this.player.sendMessage("§4エラーが発生しました．管理者に連絡してください：ブリブリガード取得失敗");
            return;
        }
        ItemStack itemStack = item.getItem();
        PlayerInventory inventory = player.getInventory();
        if(item instanceof BuriBuriGuard buriburiGuard){
            this.buriBuriGuard = buriburiGuard;
            this.buriBuriGuard.setPlayer(game, player);
        }
        inventory.addItem(itemStack);
        return;
    }


    @Override
    public boolean isAchieved() {
        List<Player> alivePlayers = this.game.getGameStatesManager().getAlivePlayers();
        if(alivePlayers.size() > 2){
            this.player.sendMessage(Component.text("§c他にも生存者がいる。"));
            return false;
        }
        if(!alivePlayers.contains(this.player)){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        int count = 0;
        for(Player p : alivePlayers) {
            if(p.equals(this.player) || p.equals(this.targetPlayer)) {
                count++;
            }
        }
        if(count == 2){
            this.player.sendMessage("§6やっと...二人きりになれたね。");
            this.targetPlayer.sendMessage("§6やっと...二人きりになれたね。");
            return true;
        }
        if(!alivePlayers.contains(this.targetPlayer)){
            this.player.sendMessage(Component.text(this.targetPlayer.getName() + "を守り抜けなかった。"));
            return false;
        }

        this.player.sendMessage("多分つぶしたと思うけどもしこのメッセージが出てきたら状況を運営に報告してください．");
        return false;
    }

}
