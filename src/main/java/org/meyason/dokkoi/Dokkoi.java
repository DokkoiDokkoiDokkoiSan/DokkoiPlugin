package org.meyason.dokkoi;

import org.bukkit.plugin.java.JavaPlugin;

import org.meyason.dokkoi.event.EventManager;
import org.meyason.dokkoi.command.CommandManager;
import org.meyason.dokkoi.game.Game;

public final class Dokkoi extends JavaPlugin {

    private static Dokkoi instance;

    public static Dokkoi getInstance() {return instance;}

    @Override
    public void onEnable() {
        instance = this;
        new EventManager(this);
        new CommandManager(this);

        new Game();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
