package org.meyason.dokkoi.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.meyason.dokkoi.game.Game;
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

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event){
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getNewEffect() == null) return;

        Job job = Game.getInstance().getGameStatesManager().getPlayerJobs().get(player);
        if (!(job instanceof Prayer prayer)) return;
        if(!prayer.isOnLREffect()) return;

        PotionEffectType type = event.getNewEffect().getType();
        // デバフ効果だけキャンセル
        if (NEGATIVE_EFFECTS.contains(type)) {
            event.setCancelled(true);
        }
    }
}
