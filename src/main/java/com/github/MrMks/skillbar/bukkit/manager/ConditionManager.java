package com.github.MrMks.skillbar.bukkit.manager;

import com.github.MrMks.skillbar.bukkit.condition.Condition;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ConditionManager {
    private static LinkedList<Condition> conditions;

    public void init(FileConfiguration config){
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
