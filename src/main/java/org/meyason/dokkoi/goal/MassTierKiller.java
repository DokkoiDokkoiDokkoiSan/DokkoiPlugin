package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goal.TierPlayerList;


public class MassTierKiller extends Goal {

    private Tier targetTier;
    public Tier getTargetTier() {return targetTier;}
    private String tierString;

    public TierPlayerList tierPlayerList;

    public MassTierKiller(){
        super("Tier Killer", "一番選択された数が多いTierのプレイヤーを全員殺害せよ！");
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
        // 一番多いtierを検索
        int tier1Count = 0;
        int tier2Count = 0;
        int tier3Count = 0;
        for(Player targetPlayer : game.getGameStatesManager().getAlivePlayers()){
            if(targetPlayer.equals(this.player)){
                continue;
            }
            Goal goal = game.getGameStatesManager().getPlayerGoals().get(targetPlayer);
            if(goal.tier == Tier.TIER_1){
                tier1Count++;
            } else if(goal.tier == Tier.TIER_2){
                tier2Count++;
            } else if(goal.tier == Tier.TIER_3){
                tier3Count++;
            }
        }
        // 一番多いtierをターゲットにする　同数なら1,2,3の順番で選択
        if(tier1Count >= tier2Count && tier1Count >= tier3Count){
            this.targetTier = Tier.TIER_1;
            this.tierString = "Tier 1";
        } else if(tier2Count >= tier3Count){
            this.targetTier = Tier.TIER_2;
            this.tierString = "Tier 2";
        } else {
            this.targetTier = Tier.TIER_3;
            this.tierString = "Tier 3";
        }
        this.player.sendMessage("§b勝利条件が§a§l" + this.tierString + "§bのプレイヤーを全員殺害せよ！");
        CustomItem item = GameItem.getItem(TierPlayerList.id);
        if(item == null){
            this.player.sendMessage("§4エラーが発生しました．管理者に連絡してください：魔女図鑑取得失敗");
            return;
        }
        ItemStack tierListItem = item.getItem();
        PlayerInventory inventory = player.getInventory();
        if(item instanceof TierPlayerList list){
            this.tierPlayerList = list;
            this.tierPlayerList.setPlayer(game, player);
        }
        inventory.addItem(tierListItem);
    }

    public void updateList(){
        if(this.tierPlayerList != null){
            this.tierPlayerList.updateList(this.targetTier);
        }
    }


    @Override
    public boolean isAchieved() {
        for(Player targetPlayer : game.getGameStatesManager().getAlivePlayers()){
            Goal goal = game.getGameStatesManager().getPlayerGoals().get(targetPlayer);
            if(targetPlayer.equals(this.player)){
                continue;
            }
            if(goal.tier == this.targetTier){
                this.player.sendMessage("§4全ての" + this.tierString + "プレイヤーを殺害できなかった。");
                return false;
            }
        }
        this.player.sendMessage(Component.text("§6全ての" + this.tierString + "プレイヤーを殺害した！"));
        return true;
    }
}
