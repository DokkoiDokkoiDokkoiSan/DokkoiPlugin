package org.meyason.dokkoi.goal;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goal.KillerList;

import java.util.List;

public class Police extends Goal {

    public KillerList killerList;

    public Police() {
        super("Police", "殺人を犯した他のプレイヤーを全員殺せ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_2;
        setDamageMultiplier(this.tier.getDamageMultiplier());
    }


    @Override
    public void addItem() {
        this.player.sendMessage("§b殺人を犯した他のプレイヤーを全員殺せ！");
        CustomItem item = GameItem.getItem(KillerList.id);
        if(item == null){
            this.player.sendMessage("§4エラーが発生しました．管理者に連絡してください：殺すリスト取得失敗");
            return;
        }
        ItemStack killerListItem = item.getItem();
        PlayerInventory inventory = player.getInventory();
        if(item instanceof KillerList list){
            this.killerList = list;
            this.killerList.setPlayer(game, player);
        }
        inventory.addItem(killerListItem);
        return;
    }

    @Override
    public boolean isAchieved() {
        List<Player> alivePlayers = this.game.getGameStatesManager().getAlivePlayers();

        List<Player> killerlayers = this.game.getGameStatesManager().getKillerList().keySet().stream().toList();
        for(Player p : alivePlayers) {
            if (killerlayers.stream().anyMatch(ap -> ap.getUniqueId().equals(p.getUniqueId()))) {
                if(p.equals(this.player)){continue;}
                this.player.sendMessage("§4失敗だ。街に暴力が蔓延している。");
                return false;
            }
        }

        this.player.sendMessage("§6よくやった。街に平和が戻った！");
        return true;
    }
}
