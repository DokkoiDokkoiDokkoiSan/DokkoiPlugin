package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.goalitem.KillerList;

import java.util.List;
import java.util.UUID;

public class Police extends Goal {

    public KillerList killerList;

    public Police() {
        super("§bPolice", "殺人を犯した他のプレイヤーを全員殺せ！");
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
        this.player.sendMessage("§2殺人を犯した他のプレイヤーを全員殺せ！");
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
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e殺すリストに記載されたプレイヤーのみ"));
        this.player.sendMessage(Component.text("§bこれ以外を殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved() {
        List<UUID> alivePlayersUUID = this.game.getGameStatesManager().getAlivePlayers();
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player.getUniqueId()))){
            this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }

        List<UUID> killerlayers = this.game.getGameStatesManager().getKillerList().keySet().stream().toList();
        for(UUID uuid : alivePlayersUUID) {
            if (killerlayers.stream().anyMatch(ap -> ap.equals(uuid))) {
                if(uuid.equals(this.player.getUniqueId())){continue;}
                this.player.sendMessage("§c失敗だ。街に暴力が蔓延している。");
                return false;
            }
        }

        this.player.sendMessage("§6よくやった。街に平和が戻った！");
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
