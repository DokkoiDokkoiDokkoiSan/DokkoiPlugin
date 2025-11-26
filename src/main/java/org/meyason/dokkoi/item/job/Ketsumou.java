package org.meyason.dokkoi.item.job;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.GameStatesManager;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.job.Explorer;
import org.meyason.dokkoi.job.Job;

import java.util.List;

public class Ketsumou extends CustomItem {

    private Game game;
    private Player player;

    public static final String id = "ketsumou";

    public Ketsumou() {
        super(id, "§9§lけつ毛(けつもう)§r", ItemStack.of(Material.PALE_HANGING_MOSS), 1);
        List<Component> lore = List.of(
                Component.text("§5冒険者が一生かけて探し求めている§9§lけつ毛§r§5。"),
                Component.text(""),
                Component.text("§b効果"),
                Component.text("§5冒険者以外が拾うと強力なデバフを受ける。")
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
    }

    public static void activate(Player picker){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        Job job = gameStatesManager.getPlayerJobs().get(picker);
        if(!(job instanceof Explorer explorer)){
            picker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 3));
            picker.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));
            gameStatesManager.addAdditionalDamage(picker, -500);
            picker.sendActionBar(Component.text("§cこれはお前の§9§lけつ毛§r§cではない。"));
            return;
        }else{
            explorer.passive(ketsumouCount(picker) + 1);
        }
    }

    public static void deactivate(Player picker){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        Job job = gameStatesManager.getPlayerJobs().get(picker);
        if(!(job instanceof Explorer explorer)){
            if(ketsumouCount(picker) - 1 > 0) return;
            picker.removePotionEffect(PotionEffectType.SLOWNESS);
            picker.removePotionEffect(PotionEffectType.GLOWING);
            gameStatesManager.addAdditionalDamage(picker, 500);
            return;
        }else {
            explorer.passive(ketsumouCount(picker) - 1);
        }
    }

    public static int ketsumouCount(Player player){
        int count = 0;
        for(ItemStack item : player.getInventory().getContents()){
            if(item == null) continue;
            if(item.getItemMeta() != null){
                CustomItem customItem = CustomItem.getItem(item);
                if(customItem instanceof Ketsumou){
                    count++;
                }
            }
        }
        return count;
    }
}
