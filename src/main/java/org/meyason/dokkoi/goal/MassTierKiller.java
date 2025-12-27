package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goalitem.TierPlayerList;

import java.util.UUID;


public class MassTierKiller extends Goal {

    private Tier targetTier;
    public Tier getTargetTier() {return targetTier;}
    private String tierString;

    public TierPlayerList tierPlayerList;

    public MassTierKiller(){
        super("§6Tier Killer", "§e一番選択された数が多いTierのプレイヤーを全員殺害せよ！", Tier.TIER_1);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        // 一番多いtierを検索
        int tier1Count = 0;
        int tier2Count = 0;
        int tier3Count = 0;
        for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
            Player targetPlayer = Bukkit.getPlayer(uuid);
            if(targetPlayer == null){
                continue;
            }
            if(targetPlayer.getUniqueId().equals(this.player.getUniqueId())){
                continue;
            }
            Goal goal = game.getGameStatesManager().getPlayerGoals().get(targetPlayer.getUniqueId());
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
        this.player.sendMessage("§e勝利条件が§a§l" + this.tierString + "§r§eのプレイヤーを全員殺害せよ！");
        try {
            CustomItem item = GameItem.getItem(TierPlayerList.id);
            ItemStack tierListItem = item.getItem();
            ItemMeta meta = tierListItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey serialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.UNIQUE_ITEM);
            String serialUUID = container.get(serialKey, PersistentDataType.STRING);
            if (item instanceof TierPlayerList list) {
                this.tierPlayerList = list;
                this.tierPlayerList.setPlayer(game, player);
                game.getGameStatesManager().addCustomItemToSerialMap(serialUUID, list);
            }
            PlayerInventory inventory = player.getInventory();
            inventory.addItem(tierListItem);
        } catch (NoGameItemException e) {
            this.player.sendMessage("§4エラーが発生しました．管理者に連絡してください：TierPlayerList取得失敗");
            return;
        }
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e勝利条件が" + this.tierString + "§bのプレイヤー全員"));
        this.player.sendMessage(Component.text("§bこれ以外を殺害するとペナルティが付与される"));
    }

    public void updateList(){
        if(this.tierPlayerList != null){
            this.tierPlayerList.updateList(this.targetTier);
        }
    }


    @Override
    public boolean isAchieved(boolean notify) {
        if(!this.game.getGameStatesManager().getAlivePlayers().contains(this.player.getUniqueId())){
            if(notify)this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        for(UUID uuid : game.getGameStatesManager().getAlivePlayers()){
            if(uuid.equals(this.player.getUniqueId())){
                continue;
            }
            Player targetPlayer = Bukkit.getPlayer(uuid);
            if(targetPlayer == null){
                continue;
            }
            if(tierPlayerList.getTargetPlayers().contains(targetPlayer)){
                if(notify)this.player.sendMessage("§c全ての" + this.tierString + "プレイヤーを殺害できなかった。");
                return false;
            }
        }
        if(notify)this.player.sendMessage(Component.text("§6全ての" + this.tierString + "プレイヤーを殺害した！"));
        return true;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return tierPlayerList.getTargetPlayers().contains(targetPlayer);
    }
}
