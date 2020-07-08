package com.github.MrMks.skillbar.bukkit.modules;

import org.bukkit.configuration.ConfigurationSection;

public class DisabledModuleUserConfig implements ModuleUserConfig {
    private ConfigurationSection section;
    @Override
    public ConfigurationSection toConfig() {
        return section;
    }

    @Override
    public void fromConfig(ConfigurationSection section) {
        this.section = section;
    }
}
