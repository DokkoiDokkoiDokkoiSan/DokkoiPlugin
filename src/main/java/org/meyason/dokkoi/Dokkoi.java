package org.meyason.dokkoi;

import com.google.inject.Inject;
import org.meyason.dokkoi.event.EventManager;
import org.meyason.dokkoi.command.CommandManager;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.item.GameItem;

import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("dokkoi")
public final class Dokkoi {

    private static Dokkoi instance;

    @Inject
    private org.spongepowered.api.event.EventManager spongeEventManager;

    public Dokkoi() {
        instance = this;
    }

    public static Dokkoi getInstance() {
        return instance;
    }

    @Listener
    public void onInitialize(final StartedEngineEvent<Server> event) {
        // Sponge 用に必要な初期化処理
        new EventManager(this, spongeEventManager);
        new CommandManager(this);
        new GameItem();
        new Game();
    }

    @Listener
    public void onShutdown(final StoppingEngineEvent<Server> event) {
    }
}

