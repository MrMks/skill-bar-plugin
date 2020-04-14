package com.github.MrMks.skillbar.bukkit;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class FileConfigStore {
    private String fileName;
    private File configFile;
    private FileConfiguration newConfig;
    private JavaPlugin plugin;
    public FileConfigStore(JavaPlugin plugin, String fileName){
        if (plugin == null || !plugin.isEnabled()) throw new IllegalArgumentException();
        this.plugin = plugin;
        this.fileName = fileName;
        this.configFile = new File(plugin.getDataFolder(), fileName);
        saveDefaultConfig();
    }

    public FileConfiguration getConfig() {
        if (this.newConfig == null) {
            this.reloadConfig();
        }

        return this.newConfig;
    }

    public void reloadConfig() {
        this.newConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            this.newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }

    public void saveConfig() {
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException var2) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, var2);
        }
    }

    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            plugin.saveResource(fileName, false);
        }
    }
}
