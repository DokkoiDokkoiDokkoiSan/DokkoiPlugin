package org.meyason.dokkoi;

import org.bukkit.plugin.java.JavaPlugin;

import org.meyason.dokkoi.event.EventManager;
import org.meyason.dokkoi.command.CommandManager;

public final class Dokkoi extends JavaPlugin {

    private static Dokkoi instance;

    public static Dokkoi getInstance() {return instance;}

    @Override
    public void onEnable() {
        instance = this;
        new EventManager(this);
        new CommandManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
