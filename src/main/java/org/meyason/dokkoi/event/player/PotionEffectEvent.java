package org.meyason.dokkoi.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.CustomItem;
import org.meyason.dokkoi.item.battleitem.RedHelmet;
import org.meyason.dokkoi.job.Job;
import org.meyason.dokkoi.job.Prayer;

import java.util.Set;

public class PotionEffectEvent implements Listener {

    // 無効化したいデバフ効果の一覧
    private static final Set<PotionEffectType> NEGATIVE_EFFECTS = Set.of(
            PotionEffectType.SLOWNESS,
            PotionEffectType.MINING_FATIGUE,
            PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.BLINDNESS,
            PotionEffectType.HUNGER,
            PotionEffectType.WEAKNESS,
            PotionEffectType.POISON,
            PotionEffectType.WITHER,
            PotionEffectType.BAD_OMEN,
            PotionEffectType.SLOW_FALLING,
            PotionEffectType.DARKNESS
    );

    //信仰者用キャンセルイベント
    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getNewEffect() == null) return;

        if(!Game.getInstance().getGameStatesManager().isInOnDisablePotionEffectPlayers(player.getUniqueId())){
            return;
        }

        PotionEffectType type = event.getNewEffect().getType();
        if(type.equals(PotionEffectType.POISON)){
            ItemStack helmet = player.getInventory().getHelmet();
            if(helmet != null){
                CustomItem customItem = CustomItem.getItem(helmet);
                if(customItem instanceof RedHelmet redHelmet){
                    return;
                }
            }
        }
        // デバフ効果だけキャンセル
        if (NEGATIVE_EFFECTS.contains(type)) {
            event.setCancelled(true);
        }
    }
}
