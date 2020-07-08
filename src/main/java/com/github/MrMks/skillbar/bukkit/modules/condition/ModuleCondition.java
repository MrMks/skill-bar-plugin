package com.github.MrMks.skillbar.bukkit.modules.condition;

import com.github.MrMks.skillbar.bukkit.modules.Module;
import com.github.MrMks.skillbar.bukkit.modules.ModuleUserConfig;

public class ModuleCondition implements Module {
    @Override
    public ModuleUserConfig getUserConfig() {
        return new ConditionUserConfig();
    }
}
