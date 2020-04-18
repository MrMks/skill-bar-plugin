package com.github.MrMks.skillbar.bukkit;

import org.bukkit.configuration.file.FileConfiguration;

public class Setting {
    private static Setting instance = new Setting();
    public static Setting getInstance() {
        return instance;
    }

    public void readConfig(FileConfiguration config){
        this.barMaxLine = config.getInt("maxBarSize") - 1;
        this.enableCondition = config.getBoolean("enableCondition");
    }

    private int barMaxLine = 0;
    public int getBarMaxLine(){
        return barMaxLine;
    }

    private boolean enableCondition = false;
    public boolean isEnableCondition() {
        return enableCondition;
    }
}
