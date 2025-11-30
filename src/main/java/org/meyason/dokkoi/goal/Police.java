package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
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
import org.meyason.dokkoi.item.goalitem.KillerList;

import java.util.List;
import java.util.UUID;

public class Police extends Goal {

    public KillerList killerList;

    public Police() {
        super("§bPolice", "§e殺人を犯した他のプレイヤーを全員殺せ！", Tier.TIER_2);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }


    @Override
    public void addItem() {
        this.player.sendMessage("§e殺人を犯した他のプレイヤーを全員殺せ！");
        try {
            CustomItem item = GameItem.getItem(KillerList.id);
            this.killerList = (KillerList) item;
            ItemStack killerListItem = item.getItem();
            ItemMeta itemMeta = killerListItem.getItemMeta();
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            NamespacedKey serialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.UNIQUE_ITEM);
            String serialUUID = container.get(serialKey, PersistentDataType.STRING);

            this.killerList.setPlayer(game, player);
            Game.getInstance().getGameStatesManager().addCustomItemToSerialMap(serialUUID, this.killerList);

            PlayerInventory inventory = player.getInventory();
            inventory.addItem(killerListItem);
        } catch (NoGameItemException e) {
            this.player.sendMessage("§4エラーが発生しました．管理者に連絡してください：殺すリスト取得失敗");
            return;
        }
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e殺すリストに記載されたプレイヤーのみ"));
        this.player.sendMessage(Component.text("§bこれ以外を殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved(boolean notify) {
        List<UUID> alivePlayersUUID = this.game.getGameStatesManager().getAlivePlayers();
        if(!alivePlayersUUID.contains(this.player.getUniqueId())){
            if(notify)this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }

        List<UUID> killerlayers = this.game.getGameStatesManager().getKillerList().keySet().stream().toList();
        for(UUID uuid : alivePlayersUUID) {
            if (killerlayers.stream().anyMatch(ap -> ap.equals(uuid))) {
                if(uuid.equals(this.player.getUniqueId())){continue;}
                if(notify)this.player.sendMessage("§c失敗だ。街に暴力が蔓延している。");
                return false;
            }
        }

        if(notify)this.player.sendMessage("§6よくやった。街に平和が戻った！");
        return true;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        List<UUID> targetPlayers = this.game.getGameStatesManager().getKillerList().keySet().stream().toList();
        if(targetPlayers.contains(targetPlayer.getUniqueId())){
            return true;
        }
        return false;
    }
}
