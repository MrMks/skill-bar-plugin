package com.github.MrMks.skillbar.bukkit.manager;

import com.github.MrMks.skillbar.bukkit.FileConfigStore;
import com.github.MrMks.skillbar.bukkit.Setting;
import com.github.MrMks.skillbar.bukkit.condition.Condition;
import com.sucy.skill.SkillAPI;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Logger;

public class ConditionManager {
    private static final Map<String, List<Condition>> map = new HashMap<>();

    public static void init(FileConfigStore store, Logger logger){
        FileConfiguration config = store.getConfig();
        if (config == null) {
            store.saveDefaultConfig();
            store.reloadConfig();
        }
        config = store.getConfig();
        if (config == null) {
            logger.warning("Can't load Condition.yml");
        }
        try {
            init(store.getConfig());
        } catch (Throwable tr) {
            map.clear();
            logger.warning("Can't parse Condition.yml");
        }
    }

    private static void init(FileConfiguration config){
        if (config == null) throw new NullPointerException();
        Set<String> keys = config.getKeys(false);
        for (String key : keys) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;
            boolean enable = section.getBoolean("enable");
            if (!enable) continue;
            int weight = section.getInt("weight");
            List<String> world = section.getStringList("conditions.world");
            world.removeIf(v->!SkillAPI.getSettings().isWorldEnabled(v));
            List<String> profession = section.getStringList("conditions.profession");
            profession.removeIf(v->!SkillAPI.isClassRegistered(v));
            if (world.isEmpty() && profession.isEmpty()) continue;
            int barSize = Math.min(Math.max(section.getInt("barSize") - 1,0), Setting.getInstance().getBarMaxLine());
            boolean fixBar = section.getBoolean("enableFixBar");
            Map<Integer, String> barList = new HashMap<>();
            ConfigurationSection barSection = section.getConfigurationSection("barList");
            if (barSection != null){
                Set<String> barKeys = barSection.getKeys(false);
                for (String skillKey : barKeys) {
                    int index = barSection.getInt(skillKey);
                    if (index >= 0 && index < barSize * 9 + 9){
                        barList.put(index,skillKey);
                    }
                }
            }
            boolean freeSlot = section.getBoolean("enableFreeSlot");
            List<Integer> freeSlots = section.getIntegerList("freeSlots");
            boolean enableSave = section.getBoolean("saveBarToDisk");
            if (freeSlots.contains(-1)) freeSlots.removeIf(v->v != -1);
            Condition condition = new Condition(key, weight,world,profession,barSize,fixBar,barList,freeSlot,freeSlots,enableSave);
            if (world.isEmpty()) map.computeIfAbsent("",k->new ArrayList<>()).add(condition);
            world.forEach(w-> map.computeIfAbsent(w, k->new ArrayList<>()).add(condition));
        }
    }

    public static void clean(){
        map.clear();
    }

    public static Optional<Condition> match(World world, List<String> professions) {
        return world == null ? Optional.empty() : match(world.getName(), professions);
    }

    public static Optional<Condition> match(String world, List<String> professions){
        Condition c = null;
        if (Setting.getInstance().isEnableCondition()) {
            List<Condition> list = new ArrayList<>(map.getOrDefault(world, Collections.emptyList()));
            list.addAll(map.getOrDefault("", Collections.emptyList()));
            for (Condition condition : list) {
                if (condition.match(world, professions)) {
                    if (c == null) c = condition;
                    else c = c.getWeight() < condition.getWeight() ? condition : c;
                }
            }
        }
        return Optional.ofNullable(c);
    }
}
