package org.meyason.dokkoi.job;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.constants.GoalList;
import org.meyason.dokkoi.constants.Tier;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.dealeritem.*;
import org.meyason.dokkoi.menu.drugrecipemenu.DrugRecipeMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrugStore extends Job {

    private int sellCount = 0;
    public int getSellCount() {
        return sellCount;
    }
    public void incrementSellCount() {
        this.sellCount++;
    }

    private int pickCount = 0;
    public int getPickCount() {
        return pickCount;
    }
    public void incrementPickCount() {
        this.pickCount++;
    }

    private final HashMap<String, String> ultimateMap = new HashMap<>() {{
        put(Tsuyokunaru.id, TotemoTsuyokunaru.id);
        put(Katakunaru.id, TotemoKatakunaru.id);
        put(Hayakunaru.id, TotemoHayakunaru.id);
        put(Kizukieru.id, TotemoKizukieru.id);
        put(Korehamaru.id, TotemoKorehamaru.id);
    }};

    public DrugStore() {
        super("薬売師", "drag_store", 1, 100);
        passive_skill_name += "§7レイノブーツ";
        normal_skill_name += "§3ヤクツクール";
        ultimate_skill_name += "§6キョウカスール";
        skillSound = Sound.BLOCK_BREWING_STAND_BREW;
        skillVolume = 1.0f;
        skillPitch = 1.0f;

        ultimateSkillSound = Sound.BLOCK_BREWING_STAND_BREW;
        ultimateSkillVolume = 1.0f;
        ultimateSkillPitch = 1.0f;
        setRemainCoolTimeSkillUltimate(100);
    }

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
        this.goals = List.of(
                GoalList.GANGSTAR,
                GoalList.MATSUMOTOKIYOSHI,
                GoalList.SUGIYAKKYOKU
        );
    }


    public void attachGoal(Goal goal){
        this.goal = goal;
        if(goal.tier == Tier.TIER_1){
            twiceCoolTimeSkill();
        }
        passive_skill_description = List.of(
                Component.text("§5『おくすり手帳』を所持。コレハマールを捨てることが出来る。"),
                Component.text("§5マップから密売人が消える度に与ダメージが1増加する。")
        );

        normal_skill_description = List.of(
                Component.text("§5薬の調合をすることができる。"),
                Component.text("§cCT " + getCoolTimeSkill() + "秒"),
                Component.text("§5薬一覧"),
                Component.text("§9ツヨクナール: §210秒間与ダメージが2増える。(レシピ:ステーキ　パンプキンパイ　生鱈)"),
                Component.text("§9カタクナール: §2次に受けるダメージを無効化する。(レシピ:焼き鳥　パン　生鮭)"),
                Component.text("§9ハヤクナール: §25秒間移動速度増加Lv2を受け取る。(レシピ:金のニンジン　生鱈　焼き豚)"),
                Component.text("§9キズキエール: §25回復する。(レシピ:金のスイカ　ベイクドポテト　生鮭)"),
                Component.text("§9コレハマール: §2薬売師以外が所持すると、移動速度低下Lv1を付与され、最大体力が20固定になる。(レシピ:金のニンジン　金のスイカ　パンプキンパイ)")
        );

        ultimate_skill_description = List.of(
                Component.text("§5持っている薬の効果を1つだけ強化することが出来る。"),
                Component.text("§cCT " + getCoolTimeSkillUltimate() + "秒"),
                Component.text("§6トテモツヨクナール: §2与ダメージ3増加を常時受け取る。"),
                Component.text("§6トテモカタクナール: §2状態異常効果を常時受けなくなる。"),
                Component.text("§6トテモハヤクナール: §2移動速度Lv2を常時受け取る。"),
                Component.text("§6トテモキズキエール: §2再生Lv3を常時受け取る。"),
                Component.text("§6トテモコレハマール: §2薬売師以外が所持すると死亡する。このアイテムでの死亡は、薬売師の殺害判定となる。")
        );
    }

    public void ready(){
    }

    public boolean onSkillTrigger(){
        DrugRecipeMenu drugRecipeMenu = new DrugRecipeMenu();
        drugRecipeMenu.sendMenu(player);

        return true;
    }

    public boolean onSkillUltimateTrigger(){
        NamespacedKey itemKey = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        List<String> drugList = new ArrayList<>();
        PlayerInventory inventory = player.getInventory();
        for (ItemStack i : inventory.getContents()) {
            if (i == null) continue;
            ItemMeta m = i.getItemMeta();
            if (m == null) continue;
            if (m.getPersistentDataContainer().has(itemKey)) {
                CustomItem c = CustomItem.getItem(i);
                if (c instanceof Katakunaru) {
                    drugList.add(Katakunaru.id);
                } else if (c instanceof Kizukieru) {
                    drugList.add(Kizukieru.id);
                } else if (c instanceof Hayakunaru) {
                    drugList.add(Hayakunaru.id);
                } else if (c instanceof Tsuyokunaru) {
                    drugList.add(Tsuyokunaru.id);
                } else if (c instanceof Korehamaru) {
                    drugList.add(Korehamaru.id);
                }
            }
        }
        if (drugList.isEmpty()) {
            player.sendActionBar(Component.text("§c強化できる薬を所持していない。"));
            return false;
        }

        // ランダムに選出
        String drugName = drugList.get((int)(Math.random() * drugList.size()));
        NamespacedKey key = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        for (ItemStack iS : player.getInventory().getContents()) {
            if (iS == null) continue;
            ItemMeta itemMeta = iS.getItemMeta();
            if (itemMeta == null) continue;
            if (itemMeta.getPersistentDataContainer().has(key)) {
                String itemName = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (itemName != null && itemName.equals(drugName)) {
                    player.getInventory().removeItem(iS);
                    String ultimateDrugName = ultimateMap.get(drugName);
                    CustomItem ultimateItem;
                    try{
                        ultimateItem = GameItem.getItem(ultimateDrugName);
                    } catch (NoGameItemException e){
                        player.sendMessage(Component.text("§4エラー:薬が見つかりません。運営にお問い合わせください。" + ultimateDrugName));
                        return false;
                    }
                    ItemStack itemStack = ultimateItem.getItem();

                    if (inventory.firstEmpty() == -1) {
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                    }
                    inventory.addItem(itemStack);
                    player.sendMessage(Component.text("§a調合完了！§e" + ultimateItem.getName() + "§aを手に入れた！"));
                }
            }
        }

        return true;
    }
}