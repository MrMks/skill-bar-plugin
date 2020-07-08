package com.github.MrMks.skillbar.bukkit.modules;

import org.bukkit.configuration.ConfigurationSection;

public interface ModuleUserConfig {
    ConfigurationSection toConfig();
    void fromConfig(ConfigurationSection section);
}
