package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goal.Tier3PlayerList;


public class Tier3Killer extends Goal {

    public Tier3Killer(){
        super("Tier3 Killer", "勝利条件がTier3のプレイヤーを全員殺害せよ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;

        this.tier = Tier.TIER_1;
        setDamageMultiplier(this.tier.getDamageMultiplier());
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§b勝利条件が§a§lTier3§bのプレイヤーを全員殺害せよ！");
        CustomItem item = GameItem.getItem(Tier3PlayerList.id);
        if(item == null){
            this.player.sendMessage("§4エラーが発生しました．管理者に連絡してください：魔女図鑑取得失敗");
            return;
        }
        ItemStack killerListItem = item.getItem();
        PlayerInventory inventory = player.getInventory();
        if(item instanceof Tier3PlayerList list){
            list.setPlayer(game, player);
            list.updateList();
        }
        inventory.addItem(killerListItem);
    }


    @Override
    public boolean isAchieved() {
        for(Player targetPlayer : game.getGameStatesManager().getAlivePlayers()){
            Goal goal = game.getGameStatesManager().getPlayerGoals().get(targetPlayer);
            if(goal.tier == Tier.TIER_3){
                this.player.sendMessage("§4全てのTier3プレイヤーを殺害できなかった。");
                return false;
            }
        }
        this.player.sendMessage(Component.text("§6全てのTier3プレイヤーを殺害した！"));
        return true;
    }
}
