package org.meyason.dokkoi.item.goalitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;

import java.util.List;

public class GoalMemo extends CustomItem implements InteractHooker {

    public static final String id = "goal_memo";

    public GoalMemo() {
        super(id, "§a勝利条件メモ", ItemStack.of(Material.PAPER), 1);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5右クリックで勝利条件を確認")
        );
        setDescription(lore);
    }

    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta != null){
                item.setItemMeta(meta);
            }
            return item;
        };
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Goal goal = Game.getInstance().getGameStatesManager().getPlayerGoals().get(player.getUniqueId());
        if(goal == null){
            player.sendMessage(Component.text("§cまだ勝利条件が選択されていません。"));
            return;
        }
        player.sendMessage(Component.text("§a===== §e§l勝利条件メモ§r§a ====="));
        player.sendMessage(Component.text("§b勝利条件： §e" + goal.getDescription()));
    }
}
