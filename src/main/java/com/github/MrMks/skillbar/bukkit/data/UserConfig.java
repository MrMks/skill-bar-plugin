package com.github.MrMks.skillbar.bukkit.data;

import com.github.MrMks.skillbar.bukkit.utils.LogUtil;
import com.github.MrMks.skillbar.bukkit.modules.ModuleManager;
import com.github.MrMks.skillbar.bukkit.modules.ModuleUserConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class UserConfig {
    private final HashMap<Integer, HashMap<Integer, String>> accountMap = new HashMap<>();
    private final HashMap<String, ModuleUserConfig> moduleUserConfigs = new HashMap<>();

    private FileConfiguration save() {
        FileConfiguration fileConfiguration = new YamlConfiguration();
        ConfigurationSection base = fileConfiguration.createSection("base");
        ConfigurationSection modules = fileConfiguration.createSection("modules");
        for (Map.Entry<Integer, HashMap<Integer, String>> entry : accountMap.entrySet()) {
            ConfigurationSection section = base.createSection(entry.getKey().toString());
            HashMap<String, ArrayList<Integer>> tmp = new HashMap<>();
            for (Map.Entry<Integer, String> sEntry : entry.getValue().entrySet()) {
                tmp.putIfAbsent(sEntry.getValue(), new ArrayList<>());
                ArrayList<Integer> list = tmp.get(sEntry.getValue());
                if (!list.contains(sEntry.getKey())) list.add(sEntry.getKey());
            }
            for (Map.Entry<String, ArrayList<Integer>> sEntry : tmp.entrySet()) {
                ArrayList<Integer> list = sEntry.getValue();
                section.set(sEntry.getKey(), (list.size() > 1 ? list : list.get(0)));
            }
        }
        for (Map.Entry<String, ModuleUserConfig> entry : moduleUserConfigs.entrySet()) {
            try {
                modules.set(entry.getKey(), entry.getValue().toConfig());
            } catch (Throwable throwable) {
                LogUtil.warn("Unable to save user-config about module: " + entry.getKey(), throwable);
            }
        }
        return fileConfiguration;
    }

    private void load(FileConfiguration config) {
        ConfigurationSection base = config.getConfigurationSection("base");
        ConfigurationSection module = config.getConfigurationSection("module");
        Set<String> accounts = base.getKeys(false);
        for (String acc : accounts) {
            try {
                Integer account = Integer.parseInt(acc);
                accountMap.putIfAbsent(account, new HashMap<>());
                HashMap<Integer, String> map = accountMap.get(account);
                ConfigurationSection section = base.getConfigurationSection(acc);
                Set<String> skillNames = section.getKeys(false);
                for (String skillName : skillNames) {
                    if (section.isInt(skillName)) {
                        map.putIfAbsent(section.getInt(skillName), skillName);
                    } else if (section.isList(skillName)) {
                        List<Integer> list = section.getIntegerList(skillName);
                        for (Integer slot : list) map.putIfAbsent(slot, skillName);
                    }
                }
            } catch (NumberFormatException e) {
                LogUtil.severe("unable to load 'base' section", e);
            }
        }
        Set<String> modules = module.getKeys(false);
        for (String moduleName : modules) {
            ModuleUserConfig muc = ModuleManager.getModule(moduleName).getUserConfig();
            muc.fromConfig(module.getConfigurationSection(moduleName));
            moduleUserConfigs.putIfAbsent(moduleName, muc);
        }
    }
}
