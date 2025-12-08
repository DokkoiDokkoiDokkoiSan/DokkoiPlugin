package org.meyason.dokkoi.event;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.event.block.*;
import org.meyason.dokkoi.event.entity.*;
import org.meyason.dokkoi.event.player.*;

public class EventManager {
    public EventManager(Dokkoi core){
        init(core);
    }

    private void init(Dokkoi core) {
        core.getServer().getPluginManager().registerEvents(new PlayerInteractManager(), core);
        core.getServer().getPluginManager().registerEvents(new DamageEvent(), core);
        core.getServer().getPluginManager().registerEvents(new ProjectileHitBlockEvent(), core);
        core.getServer().getPluginManager().registerEvents(new LaunchEvent(), core);
        core.getServer().getPluginManager().registerEvents(new DespawnEvent(), core);
        core.getServer().getPluginManager().registerEvents(new LogoutEvent(), core);
        core.getServer().getPluginManager().registerEvents(new PickEvent(), core);
        core.getServer().getPluginManager().registerEvents(new PotionEffectEvent(), core);
        core.getServer().getPluginManager().registerEvents(new EntityInteractEvent(), core);
        core.getServer().getPluginManager().registerEvents(new LoginEvent(), core);
        core.getServer().getPluginManager().registerEvents(new ChatEvent(), core);
        core.getServer().getPluginManager().registerEvents(new ItemFrameProtect(), core);
        core.getServer().getPluginManager().registerEvents(new MobCombustProtect(), core);
        core.getServer().getPluginManager().registerEvents(new EntityDeathEvent(), core);
        core.getServer().getPluginManager().registerEvents(new BlockInteractEvent(), core);
        core.getServer().getPluginManager().registerEvents(new GunSwapEvent(), core);
    }
}
