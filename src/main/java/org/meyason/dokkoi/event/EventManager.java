package org.meyason.dokkoi.event;

import org.bukkit.plugin.PluginManager;
import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.event.block.*;
import org.meyason.dokkoi.event.entity.*;
import org.meyason.dokkoi.event.player.*;

public class EventManager {
    public EventManager(Dokkoi core){
        init(core);
    }

    private void init(Dokkoi core) {
        PluginManager pm = core.getServer().getPluginManager();
        pm.registerEvents(new PlayerInteractManager(), core);
        pm.registerEvents(new DamageEvent(), core);
        pm.registerEvents(new ProjectileHitBlockEvent(), core);
        pm.registerEvents(new LaunchEvent(), core);
        pm.registerEvents(new DespawnEvent(), core);
        pm.registerEvents(new LogoutEvent(), core);
        pm.registerEvents(new PickEvent(), core);
        pm.registerEvents(new PotionEffectEvent(), core);
        pm.registerEvents(new EntityInteractEvent(), core);
        pm.registerEvents(new LoginEvent(), core);
        pm.registerEvents(new ChatEvent(), core);
        pm.registerEvents(new ItemFrameProtect(), core);
        pm.registerEvents(new MobCombustProtect(), core);
        pm.registerEvents(new EntityDeathEvent(), core);
        pm.registerEvents(new ChestInteractEvent(), core);
        pm.registerEvents(new GunSwapEvent(), core);
        pm.registerEvents(new FlowerProtect(), core);
        pm.registerEvents(new CraftProtect(), core);
        pm.registerEvents(new ChairInteractEvent(), core);
        pm.registerEvents(new ShiftEvent(), core);
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
        core.getServer().getPluginManager().registerEvents(new ChickenSpawnProtect(), core);
        core.getServer().getPluginManager().registerEvents(new ChestInteractEvent(), core);
        core.getServer().getPluginManager().registerEvents(new GunSwapEvent(), core);
        core.getServer().getPluginManager().registerEvents(new FlowerProtect(), core);
        core.getServer().getPluginManager().registerEvents(new CraftProtect(), core);
        core.getServer().getPluginManager().registerEvents(new ChairInteractEvent(), core);
    }
}
