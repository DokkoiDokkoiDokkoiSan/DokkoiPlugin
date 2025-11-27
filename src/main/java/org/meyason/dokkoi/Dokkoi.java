package org.meyason.dokkoi;

import com.google.inject.Inject;
import org.meyason.dokkoi.event.GameEventManager;
import org.meyason.dokkoi.command.CommandManager;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.GameItem;

import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import org.slf4j.Logger;

@Plugin("dokkoi")
public final class Dokkoi {

    @Inject
    private Logger logger;

    private static Dokkoi instance;

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    private org.spongepowered.api.event.EventManager spongeEventManager;

    public Dokkoi() {
        instance = this;
    }

    public static Dokkoi getInstance() {
        return instance;
    }

    @Listener
    public void refresh(RefreshGameEvent event){
        logger.info("[Dokkoi] Refreshed");
        new GameEventManager(pluginContainer, spongeEventManager);
        new CommandManager(this);
        new GameItem();
        new Game();
    }

    @Listener
    public void onStrarting(final StartingEngineEvent<Server> event) {
        logger.info("[Dokkoi] Starting");
        new GameEventManager(pluginContainer, spongeEventManager);
        new CommandManager(this);
        new GameItem();
    }

    @Listener
    public void onInitialize(final StartedEngineEvent<Server> event) {
        logger.info("[Dokkoi] Booted");
        new Game();
    }

    @Listener
    public void onShutdown(final StoppingEngineEvent<Server> event) {
    }
}

