package com.github.MrMks.skillbar.bukkit.condition;

import com.github.MrMks.skillbar.common.ICondition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Condition implements ICondition {
    private String conditionKey;
    private boolean enable;
    private int weight;
    private List<String> worlds;
    private List<String> professionKeys;
    private int barSize;
    private boolean enableFix;
    private Map<Integer,String> list;
    private boolean enableFree;
    private List<Integer> freeList;
    private boolean save;

    public Condition(String key, boolean enable, int weight, List<String> worlds, List<String> professionKeys, int barSize, boolean enableFix, Map<Integer, String> list, boolean enableFree, List<Integer> freeList, boolean save){
        this.conditionKey = key;
        this.enable = enable;
        this.weight = weight;
        this.worlds = worlds;
        this.professionKeys = professionKeys;
        this.barSize = barSize;
        this.enableFix = enableFix;
        this.list = enableFix ? list : Collections.emptyMap();
        this.enableFree = enableFix && enableFree;
        this.freeList = this.enableFree ? freeList : Collections.emptyList();
        this.save = (!this.enableFix || this.isEnableFree()) && save;
    }

    public boolean match(String world, List<String> professions){
        return (worlds.isEmpty() || worlds.contains(world)) && (professions.isEmpty() || professions.removeAll(professionKeys));
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

    public Map<Integer, String> getFixMap() {
        return list;
    }

    public boolean isEnableFree(){
        return enableFree;
    }

    public List<Integer> getFreeList(){
        return freeList;
    }

    public boolean isSave(){
        return this.save;
    }
}
