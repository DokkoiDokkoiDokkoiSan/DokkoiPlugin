package org.meyason.dokkoi.item.battleitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;

import java.util.List;

public class PotionBottleEmpty extends CustomItem {

    public static final String id = "potion_bottle_empty";

    public PotionBottleEmpty() {
        super(id, "詰め替えポーション(空)", ItemStack.of(Material.GLASS_BOTTLE), 1);
        List<Component> lore = List.of(
                Component.text("§5中身が空になった詰め替えポーション。"),
                Component.text("§5ショップおじいちゃんに話しかけて回復してもらおう。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5インベントリに入った状態でショップおじいちゃんに話しかけると"),
                Component.text("§5体力が全回復する詰め替えポーションをもらえる。")
        );
        setDescription(lore);
    }


    @Override
    protected void registerItemFunction() {
        default_setting = (item) -> {
            ItemMeta meta = item.getItemMeta();
            if(meta instanceof PotionMeta potionMeta){
                potionMeta.setBasePotionType(PotionType.HEALING);
                item.setItemMeta(potionMeta);
            }
            return item;
        };
    }

    public static void activate(Player player, ItemStack itemStack) {
        player.sendMessage(Component.text("§a詰め替えポーションが補充された！"));

        itemStack.setAmount(0);
        CustomItem fullPotionBottle;
        try{
            fullPotionBottle = GameItem.getItem(PotionBottleFull.id);
        } catch (NoGameItemException e){
            player.sendMessage(Component.text("§4エラー: 詰め替えポーションが見つかりません。管理者に報告してください。"));
            return;
        }
        ItemStack fullPotionBottleItem = fullPotionBottle.getItem();
        fullPotionBottleItem.setAmount(1);
        player.getInventory().addItem(fullPotionBottleItem);
    }
}
