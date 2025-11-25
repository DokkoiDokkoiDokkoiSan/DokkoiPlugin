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
        core.getCommand("goalfix").setExecutor(new GoalFixCommand());
        core.getCommand("spawnentity").setExecutor(new SpawnEntityCommand());
    }
}
