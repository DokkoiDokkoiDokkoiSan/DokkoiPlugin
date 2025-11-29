package org.meyason.dokkoi.item.goalitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameState;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.goal.Defender;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;

import java.util.List;

public class BuriBuriGuard extends CustomItem {

    public static final String id = "buri_buri_guard";

    private Player player;
    private Game game;

    private Player targetPlayer = player;

    public BuriBuriGuard(){
        super(id, "§aブリブリガード", ItemStack.of(Material.WOODEN_SWORD), 1);
        isUnique = true;
        List<Component> lore = List.of(
                Component.text("§5ぶりぶりしている侍が使っている剣を模して作られた剣。めっちゃ人守れる。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5攻撃力2、剣を使用すると10秒間勝利条件で指定されているプレイヤーが受けるダメージを0にする。"),
                Component.text("§cCT 20秒")
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

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        player.sendMessage(Component.text("§aブリブリガード§bを手に入れた！"));
        Goal goal = game.getGameStatesManager().getPlayerGoals().get(player.getUniqueId());
        if(goal instanceof Defender defender){
            this.targetPlayer = defender.getTargetPlayer();
        }
    }

    public void skill(Player player, Player targetPlayer){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        if(gameStatesManager.getItemCoolDownScheduler().containsKey(player.getUniqueId())){
            player.sendMessage("§cクールタイム中です");
            return;
        }
        gameStatesManager.addDamageCutPercent(targetPlayer.getUniqueId(), 100);
        BukkitRunnable itemInitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(gameStatesManager.getGameState() != GameState.IN_GAME) {
                    cancel();
                    return;
                }
                if (!player.isOnline() || !targetPlayer.isOnline() || !gameStatesManager.getItemCoolDownScheduler().containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }
                gameStatesManager.removeItemCoolDownScheduler(player.getUniqueId());
                gameStatesManager.calcDamageCutPercent(targetPlayer.getUniqueId(), -100);
            }
        };
        itemInitTask.runTaskLater(Dokkoi.getInstance(), 10 * 20L);
        gameStatesManager.addItemCoolDownScheduler(player.getUniqueId(), itemInitTask);
    }

}
