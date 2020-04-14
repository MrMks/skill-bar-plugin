package com.github.MrMks.skillbar.bukkit.condition;

import java.util.List;
import java.util.Map;

public class Condition {
    private String conditionKey;
    private boolean enable;
    private int weight;
    private List<String> worlds;
    private List<String> professionKeys;
    private int barSize;
    private boolean enableFix;
    private Map<Integer,String> list;

    public Condition(String key, boolean enable, int weight, List<String> worlds, List<String> professionKeys, int barSize, boolean enableFix, Map<Integer, String> list){
        this.conditionKey = key;
        this.enable = enable;
        this.weight = weight;
        this.worlds = worlds;
        this.professionKeys = professionKeys;
        this.barSize = barSize;
        this.enableFix = enableFix;
        this.list = list;
    }

    public boolean match(String world, List<String> professions){
        return worlds.contains(world) && professions.removeAll(professionKeys);
    }

    public String getKey(){
        return conditionKey;
    }

    public int getWeight(){
        return weight;
    }

    public int getBarSize() {
        return barSize;
    }

    public boolean isEnableFix() {
        return enableFix;
    }

    public Map<Integer, String> getBarList() {
        return list;
    }
}
