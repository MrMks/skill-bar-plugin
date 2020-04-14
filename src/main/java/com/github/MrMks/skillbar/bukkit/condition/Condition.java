package com.github.MrMks.skillbar.bukkit.condition;

import java.util.List;
import java.util.Map;

public class Condition {
    private String conditionKey;
    private boolean enable;
    private int weight;
    private List<String> worlds;
    private List<String> professionKeys;
    private boolean enableFix;
    private Map<String, Integer> list;

    public boolean match(String world, List<String> professions){
        return worlds.contains(world) && professions.removeAll(professionKeys);
    }

    public int getWeight(){
        return weight;
    }
}
