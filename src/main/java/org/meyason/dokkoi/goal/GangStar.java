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
import org.meyason.dokkoi.item.goalitem.UnkillerList;

public class GangStar extends Goal{

    private UnkillerList unKillerList;

    public GangStar() {
        super("§6GangStar", "§e街の治安をめちゃくちゃにしてやれ！", Tier.TIER_1);
    }

    @Override
    public void setGoal(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void addItem() {
        this.player.sendMessage("§e街の治安をめちゃくちゃにしてやれ！");
        CustomItem item;
        try {
            item = GameItem.getItem(UnkillerList.id);
        }catch (NoGameItemException e) {
            this.player.sendMessage("§4エラーが発生しました．管理者に連絡してください：殺してないノート取得失敗");
            return;
        }
        this.unKillerList = (UnkillerList) item;
        ItemStack unKillerListItem = item.getItem();
        ItemMeta itemMeta = unKillerListItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey serialKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.UNIQUE_ITEM);
        String serialUUID = container.get(serialKey, PersistentDataType.STRING);

        this.unKillerList.setPlayer(game, player);
        Game.getInstance().getGameStatesManager().addCustomItemToSerialMap(serialUUID, this.unKillerList);

        PlayerInventory inventory = player.getInventory();
        inventory.addItem(unKillerListItem);

        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e殺してないリストに記入されているプレイヤーのみ"));
        return;
    }

    @Override
    public boolean isAchieved(boolean notify) {
        if(!this.game.getGameStatesManager().getAlivePlayers().contains(this.player.getUniqueId())){
            if(notify)this.player.sendMessage("§cお前はもう死んでいる。");
            return false;
        }
        if(getUnKillerList().getTargetPlayerList().isEmpty()){
            if(notify)this.player.sendMessage("§cよくやった！お前は街の治安をめちゃくちゃにした。");
            return true;
        }
        if(notify)this.player.sendMessage("§c失敗だ。ちきってるやつがまだ生きている。");
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        if(getUnKillerList().getTargetPlayerList().contains(targetPlayer.getUniqueId())){
            return true;
        }
        return false;
    }

    public UnkillerList getUnKillerList(){
        return this.unKillerList;
    }
}
