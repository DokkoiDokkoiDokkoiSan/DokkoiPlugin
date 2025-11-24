package org.meyason.dokkoi.item.gacha;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.constants.GameItemKeyString;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.goal.GachaAddict;
import org.meyason.dokkoi.goal.Goal;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.gacha.menu.GachaPointMenu;

public class GachaMachine extends CustomItem {

    public static final String id = "gacha_machine";

    public GachaMachine() {
        super(id, "Gacha Machine", ItemStack.of(Material.COAL), 1);
        isUnique = true;
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

    public static ItemStack doGacha(Player player){
        if(!hasEnoughGachaPoint(player)){
            return null;
        }
        double result = Math.random();
        String rarity = "";
        if(result < GachaAddict.ssrRate){
            rarity = GachaAddict.SSR;
        }else if(result < GachaAddict.srRate + GachaAddict.ssrRate) {
            rarity = GachaAddict.SR;
        }else{
            rarity = GachaAddict.R;
        }
        Material material = GachaAddict.getRandomGachaResult(rarity);
        player.sendMessage("§aガチャを回して§6[" + rarity + "]§aの§e[" + GachaAddict.nameMap.get(material) + "]§aを手に入れた！");
        return ItemStack.of(material);
    }

    public static boolean hasEnoughGachaPoint(Player player){
        Game game = Game.getInstance();
        Goal goal = game.getGameStatesManager().getPlayerGoals().get(player);
        if(goal instanceof GachaAddict gachaAddict){
            int gachaPoint = gachaAddict.getGachaPoint();
            if(gachaPoint >= GachaAddict.gachaCost){
                gachaAddict.setGachaPoint(gachaPoint - GachaAddict.gachaCost);
                game.updateScoreboardDisplay(player);
                return true;
            }
        }
        player.sendMessage("§4ガチャポイントが足りません");
        return false;
    }

    public static void exchangeGachaPoint(Player player, Material material){
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = new ItemStack(material, 1);
        if(inventory.containsAtLeast(itemStack, 1)){
            Game game = Game.getInstance();
            Goal goal = game.getGameStatesManager().getPlayerGoals().get(player);
            if(goal instanceof GachaAddict gachaAddict){
                inventory.removeItem(itemStack);
                int gachaPoint = gachaAddict.getGachaPoint();
                int addPoint = GachaAddict.pointMap.get(material);
                gachaAddict.setGachaPoint(gachaPoint + addPoint);
                game.updateScoreboardDisplay(player);
                player.sendMessage("§aガチャポイントを§e" + addPoint + "§a獲得した！");

                GachaPointMenu menu = new GachaPointMenu();
                menu.sendMenu(player);
            }
        }else {
            player.sendMessage("§4そのアイテムを所持していません");
        }
    }

}
