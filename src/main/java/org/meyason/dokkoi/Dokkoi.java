package org.meyason.dokkoi;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.meyason.dokkoi.database.DatabaseConnector;
import org.meyason.dokkoi.event.EventManager;
import org.meyason.dokkoi.command.CommandManager;
import org.meyason.dokkoi.event.network.DebugPacketListener;
import org.meyason.dokkoi.file.Config;
import org.meyason.dokkoi.game.Game;
import org.meyason.dokkoi.game.LPManager;
import org.meyason.dokkoi.item.GameItem;
import org.meyason.dokkoi.scheduler.PacketScheduler;

public final class Dokkoi extends JavaPlugin {

    private static Dokkoi instance;

    public static Dokkoi getInstance() {return instance;}

    private DatabaseConnector databaseConnector;

    private Config config;

    private LPManager lpManager;

    public LPManager getLPManager() {return lpManager;}

    @Override
    public void onEnable() {
        instance = this;
        this.config = new Config(this);
        this.databaseConnector = new DatabaseConnector(
                config.getDBHost(),
                config.getDBPort(),
                config.getDBDatabase(),
                config.getDBUserName(),
                config.getDBUserPassword()
        );
        new DokkoiDatabaseAPI(databaseConnector);
        this.lpManager = new LPManager();
        new EventManager(this);
        new CommandManager(this);
        new GameItem();
        new PacketScheduler().runTaskTimer(this, 0, 1);
        new Game();
        // PacketListener for debugging, developing;
        // new DebugPacketListener().register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
