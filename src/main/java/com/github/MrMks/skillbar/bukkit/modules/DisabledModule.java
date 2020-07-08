package com.github.MrMks.skillbar.bukkit.modules;

public class DisabledModule implements Module {
    @Override
    public ModuleUserConfig getUserConfig() {
        return new DisabledModuleUserConfig();
    }
}
