package com.github.MrMks.skillbar.bukkit.manager;

import com.github.MrMks.skillbar.bukkit.FileConfigStore;
import com.github.MrMks.skillbar.bukkit.condition.Condition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConditionManager {
    private static LinkedList<Condition> conditions = new LinkedList<>();
    public static void init(FileConfigStore store){
        FileConfiguration config = store.getConfig();
        if (config == null) {
            store.saveDefaultConfig();
            store.reloadConfig();
        }
        config = store.getConfig();
        conditions.clear();
        Set<String> keys = config.getKeys(false);
        for (String key : keys) {
            ConfigurationSection section = config.getConfigurationSection(key);
            boolean enable = section.getBoolean("enable");
            int wright = section.getInt("enable");
            List<String> world = section.getStringList("conditions.world");
            List<String> profession = section.getStringList("conditions.profession");
            int barSize = section.getInt("barSize");
            boolean fixBar = section.getBoolean("enableBarList");
            Map<String, Integer> barList = new HashMap<>();
            ConfigurationSection barSection = section.getConfigurationSection("barList");
            Set<String> barKeys = barSection.getKeys(false);
            for (String skillKey : barKeys) {
                int index = barSection.getInt(skillKey);
                if (index >= 0 && index < barSize * 9){
                    barList.put(skillKey, index);
                }
            }
            conditions.add(new Condition(key,enable,wright,world,profession,barSize,fixBar,barList));
        }
    }

    public static void clean(){
        conditions.clear();
    }

    public static Optional<Condition> match(String world, List<String> professions){
        Condition c = null;
        for (Condition condition : conditions){
            if (condition.match(world, professions)) {
                if (c == null) c = condition;
                else c = c.getWeight() < condition.getWeight() ? condition : c;
            }
        }
        return Optional.ofNullable(c);
    }
}