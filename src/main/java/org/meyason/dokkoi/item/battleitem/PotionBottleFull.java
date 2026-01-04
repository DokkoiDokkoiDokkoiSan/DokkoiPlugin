package org.meyason.dokkoi.item.battleitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.itemhooker.InteractHooker;

import java.util.List;

public class PotionBottleFull extends CustomItem implements InteractHooker {

    public static final String id = "potion_bottle_full";

    public PotionBottleFull() {
        super(id, "§a詰め替えポーション", ItemStack.of(Material.POTION), 1);
        List<Component> lore = List.of(
                Component.text("§5別の世界から輸入してくる時に時間がかかりすぎて変色したらしい。"),
                Component.text("§5元々は緑色だったんだとか。"),
                Component.text("§5使用後は詰め替えポーション(空)に変わる。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5使用すると体力を全回復する。")
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

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(player.getHealth() == player.getMaxHealth()){
            player.sendActionBar(Component.text("§c既に最大体力です。"));
            return;
        }
        player.setHealth(player.getMaxHealth());
        player.sendMessage(Component.text("§a詰め替えポーションを使用した！"));

        item.setAmount(0);
        CustomItem emptyPotionBottle;
        try{
            emptyPotionBottle = GameItem.getItem(PotionBottleEmpty.id);
        } catch (NoGameItemException e){
            player.sendMessage(Component.text("§4エラー: 詰め替えポーション(空)が見つかりません。管理者に報告してください。"));
            return;
        }
        ItemStack emptyItemStack = emptyPotionBottle.getItem();
        emptyItemStack.setAmount(1);
        player.getInventory().addItem(emptyItemStack);
    }
}
