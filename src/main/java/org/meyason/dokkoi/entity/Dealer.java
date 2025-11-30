package org.meyason.dokkoi.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.job.DrugStore;
import org.meyason.dokkoi.job.Job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Dealer extends GameEntity {

    private static final String HAYAKUNARU = Hayakunaru.id;
    private static final String KATAKUNARU = Katakunaru.id;
    private static final String KIZUKIERU = Kizukieru.id;
    private static final String KOREHAMARU = Korehamaru.id;
    private static final String TSUYOKUNARU = Tsuyokunaru.id;

    private final List<String> dragList = List.of(
            TSUYOKUNARU,
            KATAKUNARU,
            KIZUKIERU,
            HAYAKUNARU,
            KOREHAMARU
    );

    private final HashMap<String, String> dragNameMap = new HashMap<>(){{
        put(TSUYOKUNARU, "§6§lツヨクナール§r");
        put(KATAKUNARU, "§6§lカタクナール§r");
        put(KIZUKIERU, "§6§lキズキエール§r");
        put(HAYAKUNARU, "§6§lハヤクナール§r");
        put(KOREHAMARU, "§6§lコレハマール§r");
    }};

    private String nowNeedDragID = dragList.getFirst();

    public Dealer() {
        super(GameEntity.DEALER);
    }

    public void talk(Player player) {
        player.sendMessage(Component.text("§d密売人『ウェッ...グェホッ...ゴホ...。" + dragNameMap.get(nowNeedDragID) + "§d...。あんた..." + dragNameMap.get(nowNeedDragID) + "§d...持ってないか？』"));
    }

    public boolean giveDrag(Player player, CustomItem item) {
        if (!item.getId().equals(nowNeedDragID)) {
            talk(player);
            return false;
        }

        int currentIndex = dragList.indexOf(nowNeedDragID);
        if(Game.getInstance().getGameStatesManager().getPlayerJobs().get(player.getUniqueId()) instanceof DrugStore drugStore){
            drugStore.incrementSellCount();
        }
        if (currentIndex + 1 < dragList.size()) {
            nowNeedDragID = dragList.get(currentIndex + 1);
        } else {
            nowNeedDragID = null;
            player.sendMessage(Component.text("§d密売人『...あ、ああ。これは...へへっ、俺のだ。俺の...』"));
            Game.getInstance().getGameStatesManager().addAdditionalDamage(player.getUniqueId(), 1);
            player.sendMessage(Component.text("§6密売人は満足し、与ダメージ+1のバフをくれた。"));
            return true;
        }
        player.sendMessage(Component.text("§d密売人『...あれが、ねえや...。あれがねえと...。" + dragNameMap.get(nowNeedDragID) + "§dがねえと意味ねえじゃねえかッ！！』"));
        return false;
    }

    public void arrested(Player player) {
        player.sendMessage(Component.text("§d密売人『私の尿から覚醒剤が検出されたということで逮捕されましたが、事実に反します。』"));
        GameStatesManager manager = Game.getInstance().getGameStatesManager();
        //生存者の役職に薬売人がいるか確認
        for(Map.Entry<UUID, Job> entry : manager.getPlayerJobs().entrySet()) {
            UUID uuid = entry.getKey();
            Job job = entry.getValue();
            if(job instanceof DrugStore) {
                if(manager.getAlivePlayers().contains(uuid)) {
                    Player jobPlayer = Bukkit.getPlayer(uuid);
                    if(jobPlayer == null) return;
                    jobPlayer.sendMessage(Component.text("§c密売人が逮捕された。与ダメージが1増加した。"));
                    manager.addAdditionalDamage(uuid, 1);
                    return;
                }
            }
        }
    }

}
