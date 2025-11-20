package org.meyason.dokkoi.goal;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.meyason.dokkoi.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Collector extends Goal {

    public int itemNumber;
    public Material targetItem;
    public List<Material> targetItemList = new ArrayList<>(){{
        add(Material.DIAMOND);
        add(Material.GOLD_INGOT);
        add(Material.IRON_INGOT);
        add(Material.EMERALD);
        add(Material.COAL);
        add(Material.REDSTONE);
        add(Material.LAPIS_LAZULI);
    }};

    public Collector() {
        super("Collector", "指定されたアイテムを指定された分だけ探せ！");
    }

    @Override
    public void setGoal(Game game, Player player) {
        Random rand = new Random();
        this.itemNumber = rand.nextInt(1, 7);
        this.targetItem = targetItemList.get(rand.nextInt(targetItemList.size()));
        this.player = player;
        this.player.sendMessage("§b指定アイテム： " + targetItem.name());
        this.player.sendMessage("§b指定個数： " + itemNumber + " 個");
    }

    @Override
    public boolean isAchieved() {
        int count = 0;
        for (ItemStack item : this.player.getInventory().getContents()) {
            if (item != null && item.getType() == this.targetItem) {
                count += item.getAmount();
            }
        }
        if (count == this.itemNumber) {
            this.player.sendMessage("よくやった！" + this.targetItem.name() + "をちょうど " + this.itemNumber + " 個集めた。");
            return true;
        } else if (count > this.itemNumber) {
            this.player.sendMessage("集めすぎだ！" + this.targetItem.name() + "を " + this.itemNumber + " 個だけ集めるんだ。");
        }
        this.player.sendMessage("なんだ、足りてないぞ。" + this.targetItem.name() + "を " + this.itemNumber + " 個集めるんだ。");
        return false;
    }
}
