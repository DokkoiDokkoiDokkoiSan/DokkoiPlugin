package org.meyason.dokkoi.goal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.jobitem.gacha.StrongestStrongestStrongestBall;

public class GamblerMaster extends Goal {

    public GamblerMaster() {
        super("§cGachaBeginner", "ガチで最強のたまたまを手に入れろ！");
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
        this.player.sendMessage(Component.text("§eガチで最強のたまたまを手に入れろ！"));
        this.player.sendMessage(Component.text("§b----------------------------"));
        this.player.sendMessage(Component.text("§b殺害できるプレイヤー： §e0 人"));
        this.player.sendMessage(Component.text("§bこれ以上殺害するとペナルティが付与される"));
        return;
    }

    @Override
    public boolean isAchieved() {
        if(this.game.getGameStatesManager().getAlivePlayers().stream().noneMatch(p -> p.equals(this.player.getUniqueId()))){
            this.player.sendMessage(Component.text("§cお前はもう死んでいる。"));
            return false;
        }
        for(ItemStack itemStack : player.getInventory()){
            if(itemStack != null && itemStack.getItemMeta() != null){
                CustomItem customItem = CustomItem.getItem(itemStack);
                if(customItem instanceof StrongestStrongestStrongestBall){
                    this.player.sendMessage(Component.text("§6今すぐパチスロに行け！"));
                    return true;
                }
            }
        }
        this.player.sendMessage(Component.text("§cギャンブラーとしてはまだまだだな。"));
        return false;
    }

    @Override
    public boolean isKillable(Player targetPlayer){
        return false;
    }
}
