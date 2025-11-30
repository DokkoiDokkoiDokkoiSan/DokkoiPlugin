package org.meyason.dokkoi.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.meyason.dokkoi.Dokkoi;

public class Config {

    private final Dokkoi instance;

    private FileConfiguration config = null;

    public Config(Dokkoi instance) {
        this.instance = instance;
        init();
    }

    private void init() {
        instance.saveDefaultConfig();
        if(config != null) {
            instance.reloadConfig();
        }
        config = instance.getConfig();
    }

    public String getDBHost(){
        return this.config.getString("db.host");
    }

    public int getDBPort(){
        return this.config.getInt("db.port");
    }

    public String getDBUserName(){
        return this.config.getString("db.username");
    }

    public String getDBUserPassword(){
        return this.config.getString("db.password");
    }

}
