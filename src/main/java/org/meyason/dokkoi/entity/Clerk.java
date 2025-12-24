package org.meyason.dokkoi.entity;

import net.kyori.adventure.text.Component;
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
import org.meyason.dokkoi.item.battleitem.*;
import org.meyason.dokkoi.item.gunitem.*;
import org.meyason.dokkoi.item.utilitem.*;
import org.meyason.dokkoi.item.weapon.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Clerk extends GameEntity {

    private String type;
    public String getType() {
        return type;
    }

    private List<UUID> bottlePlayers;

    public static final HashMap<String, Integer> itemPrices = new HashMap<>() {{
        put(IdiotDetector.id, 2);
        put(FortuneBall.id, 5);
        put(PotionBottleFull.id, 3);
        put(InstantDevour.id, 3);

        put(HealingCrystal.id, 1);
        put(ArcherArmor.id, 2);
        put(EdenChime.id, 5);
        put(FragGrenade.id, 3);

        put(LongSword.id, 5);
        put(DrainBrade.id, 10);
        put(DragonBrade.id, 10);

        put(NormalBow.id, 4);
        put(RedBow.id, 9);
        put(BlueBow.id, 14);
        put(Arrow.id, 1);

        put(Pistol.id, 11);
        put(Stinger.id, 17);
        put(DrH.id, 23);
        put(HGMagazine.id, 2);
        put(SMGMagazine.id, 2);
        put(ARMagazine.id, 2);
    }};

    public static final List<String> availableItems = List.of(
            IdiotDetector.id,
            FortuneBall.id,
            PotionBottleFull.id,
            InstantDevour.id,

            HealingCrystal.id,
            ArcherArmor.id,
            EdenChime.id,
            FragGrenade.id,

            LongSword.id,
            DrainBrade.id,
            DragonBrade.id,

            NormalBow.id,
            RedBow.id,
            BlueBow.id,
            Arrow.id,

            Pistol.id,
            Stinger.id,
            DrH.id,
            HGMagazine.id,
            SMGMagazine.id,
            ARMagazine.id
    );

    public Clerk(String type) {
        super(GameEntity.CLERK);
        this.type = type;
    }

    public void talk(Player player) {
        player.sendMessage(Component.text(GameEntity.talkMessageMap.get(type)));
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
            player.sendMessage(Component.text(GameEntity.notEnoughMoneyMessageMap.get(type)));
            return false;
        }else{
            CustomItem customItem;
            try{
                customItem = GameItem.getItem(itemId);
            }catch(NoGameItemException e){
                player.sendMessage(Component.text(GameEntity.getGameItemFailedMessageMap.get(type)));
                return false;
            }
            ItemStack itemStack = customItem.getItem();
            if(customItem instanceof Arrow){
                itemStack.setAmount(32);
            }
            // インベントリに空きがあるか判定
            int emptySlot = player.getInventory().firstEmpty();
            if(emptySlot == -1) {
                player.sendMessage(Component.text(GameEntity.notEnoughInventoryMessageMap.get(type)));
                return false;
            }
            // モネイを減らす
            int remainingMoney = price;
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
            player.sendMessage(Component.text(GameEntity.buyItemMessageMap.get(type)));
            return true;
        }
    }

    public boolean isAlreadyRefilled(Player player){
        player.sendMessage(Component.text(GameEntity.getGameItemFailedMessageMap.get(type)));
        return bottlePlayers.contains(player.getUniqueId());
    }

    public void addRefilledPlayer(Player player){
        bottlePlayers.add(player.getUniqueId());
    }
}
