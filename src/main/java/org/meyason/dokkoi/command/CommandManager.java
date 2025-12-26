package org.meyason.dokkoi.command;

import org.meyason.dokkoi.Dokkoi;

public class CommandManager {

    private final Dokkoi core;

    public CommandManager(Dokkoi core) {
        this.core = core;
        init(core);
    }

    private void init(Dokkoi core){
        core.getCommand("matching").setExecutor(new MatchingStartCommand());
        core.getCommand("end").setExecutor(new GameEndCommand());
        core.getCommand("spawnentity").setExecutor(new SpawnEntityCommand());
        core.getCommand("getitem").setExecutor(new GetItemCommand());
        core.getCommand("addLP").setExecutor(new AddLPCommand());
        core.getCommand("edit").setExecutor(new EditModeCommand());
        core.getCommand("itemInfo").setExecutor(new InfoCommand());
    }
}
