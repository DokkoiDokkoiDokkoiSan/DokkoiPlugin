package org.meyason.dokkoi.entity;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.exception.NoGameItemException;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.item.battleitem.ArcherArmor;
import org.meyason.dokkoi.item.battleitem.HealingCrystal;
import org.meyason.dokkoi.item.utilitem.Monei;
import org.meyason.dokkoi.item.weapon.LongSword;

import java.util.HashMap;

public class Clerk extends GameEntity {

    public static final HashMap<String, Integer> itemPrices = new HashMap<>() {{
        put(HealingCrystal.id, 1);
        put(ArcherArmor.id, 2);
        put(LongSword.id, 5);
        put(Material.ARROW.name(), 1);
    }};

    public Clerk() {
        super(GameEntity.CLERK);
    }

    public void talk(Player player) {
        player.sendMessage(Component.text("§bショップおじいちゃん「入って、どうぞ。ゆっくり見てけよ見てけよ～」"));
    }

    public int getItemPrice(String itemId) {
        return itemPrices.getOrDefault(itemId, -1);
    }

    public boolean canBuyItem(Player player, String itemId) {
        int price = getItemPrice(itemId);
        int moneyCount = 0;
        NamespacedKey key = new NamespacedKey(Dokkoi.getInstance(), GameItemKeyString.ITEM_NAME);
        for(ItemStack stack : player.getInventory().getContents()){
            if(stack == null) continue;
            ItemMeta meta = stack.getItemMeta();
            if(meta == null) continue;
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if(!container.has(key)) {
                continue;
            }
            String itemName = container.get(key, PersistentDataType.STRING);
            if(itemName != null && itemName.equals(Monei.id)){
                moneyCount += stack.getAmount();
            }
        }
        if(moneyCount < price){
            player.sendMessage("§bショップおじいちゃん「そちら、14万3000円になっております。」");
            return false;
        }else{
            CustomItem customItem;
            try{
                customItem = GameItem.getItem(itemId);
            }catch(NoGameItemException e){
                player.sendMessage("§bショップおじいちゃん「(商品が)ないです。」");
                return false;
            }
            ItemStack itemStack = customItem.getItem();
            if(itemStack.getType() == Material.ARROW){
                itemStack.setAmount(32);
            }
            // インベントリに空きがあるか判定
            int emptySlot = player.getInventory().firstEmpty();
            if(emptySlot == -1) {
                player.sendMessage("§bショップおじいちゃん「あっおい待てい、インベントリに空きがないゾ」");
                return false;
            }
            // モネイを減らす
            int remainingMoney = moneyCount - price;
            for(ItemStack stack : player.getInventory().getContents()){
                if(stack == null) continue;
                ItemMeta meta = stack.getItemMeta();
                if(meta == null) continue;
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if(!container.has(key)) {
                    continue;
                }
                String itemName = container.get(key, PersistentDataType.STRING);
                if(itemName != null && itemName.equals(Monei.id)){
                    int stackAmount = stack.getAmount();
                    if(remainingMoney >= stackAmount){
                        remainingMoney -= stackAmount;
                        player.getInventory().removeItem(stack);
                    }else{
                        stack.setAmount(stackAmount - remainingMoney);
                        remainingMoney = 0;
                        break;
                    }
                }
            }

            player.getInventory().addItem(itemStack);
            player.sendMessage("§bショップおじいちゃん「ありがとナス！またいいよ！こいよ！」");
            return true;
        }
    }
}
