package org.meyason.dokkoi.event;

import org.meyason.dokkoi.Dokkoi;
import org.meyason.dokkoi.event.block.ProjectileHitBlockEvent;
import org.meyason.dokkoi.event.entity.DespawnEvent;
import org.meyason.dokkoi.event.entity.LaunchEvent;
import org.meyason.dokkoi.event.player.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.plugin.PluginContainer;

public class GameEventManager {
    public GameEventManager(PluginContainer pluginContainer, EventManager spongeEventManager) {
        init(pluginContainer, spongeEventManager);
    }

    private void init(PluginContainer pluginContainer, EventManager spongeEventManager) {
//        core.getServer().getPluginManager().registerEvents(new DamageEvent(), core);
////        core.getServer().getPluginManager().registerEvents(new SneakEvent(), core);
////        core.getServer().getPluginManager().registerEvents(new JumpEvent(), core);
//        core.getServer().getPluginManager().registerEvents(new InteractEvent(), core);
//        core.getServer().getPluginManager().registerEvents(new ProjectileHitBlockEvent(), core);
//        core.getServer().getPluginManager().registerEvents(new LaunchEvent(), core);
//        core.getServer().getPluginManager().registerEvents(new DespawnEvent(), core);
//        core.getServer().getPluginManager().registerEvents(new LogoutEvent(), core);
//        core.getServer().getPluginManager().registerEvents(new PickEvent(), core);
        EventListener<MoveEntityEvent> jumpEventListener = new JumpEvent();
        EventListenerRegistration registration = EventListenerRegistration
                .builder(MoveEntityEvent.class)
                .listener(jumpEventListener)
                .plugin(pluginContainer)
                .build();
        spongeEventManager.registerListener(registration);

    }
}
