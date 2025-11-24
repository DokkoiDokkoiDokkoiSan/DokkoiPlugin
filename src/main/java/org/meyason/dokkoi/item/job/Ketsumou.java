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

import java.time.Duration;

public class Ketsumou extends CustomItem {

    private Game game;
    private Player player;

    public static final String id = "ketsumou";

    public Ketsumou() {
        super(id, "けつ毛(けつもう", ItemStack.of(Material.PALE_HANGING_MOSS), 1);
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

    public void setPlayer(Game game, Player player){
        this.game = game;
        this.player = player;
    }

    public static void activate(Player picker){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        Job job = gameStatesManager.getPlayerJobs().get(picker);
        if(!(job instanceof Explorer)){
            picker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 3));
            picker.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));
            gameStatesManager.addAdditionalDamage(picker, -500);
            picker.sendMessage(Component.text("§c§lこれはお前のけつ毛ではない。"));
            return;
        }
    }

    public static void deactivate(Player picker){
        GameStatesManager gameStatesManager = Game.getInstance().getGameStatesManager();
        Job job = gameStatesManager.getPlayerJobs().get(picker);
        if(!(job instanceof Explorer)){
            picker.removePotionEffect(PotionEffectType.SLOWNESS);
            picker.removePotionEffect(PotionEffectType.GLOWING);
            gameStatesManager.addAdditionalDamage(picker, 500);
            return;
        }
    }
}
