package com.github.MrMks.skillbar.bukkit.data;

import com.github.MrMks.skillbar.bukkit.Setting;
import com.github.MrMks.skillbar.bukkit.condition.Condition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientAccount {
    private Condition condition;
    private final Map<String, Map<Integer, String>> conditionBar = new HashMap<>();
    private final Map<Integer, String> accountBar = new HashMap<>();
    private final Map<Integer, String> tempBar = new HashMap<>();
    public ClientAccount(){}

    public Optional<Condition> getCondition() {
        return Optional.ofNullable(condition);
    }

    public void setCondition(Condition condition){
        if (condition == null) tempBar.clear();
        else {
            if (this.condition == null || !this.condition.getKey().equals(condition.getKey())) {
                tempBar.clear();
                if (!condition.isEnableFix() || condition.isEnableFree()) {
                    if (condition.isSave()) {
                        if (!conditionBar.containsKey(condition.getKey())) conditionBar.put(condition.getKey(), new HashMap<>(condition.getFixMap()));
                    }
                    else tempBar.putAll(condition.getFixMap());
                }
            }
        }
        this.condition = condition;
    }

    public Map<Integer, String> getBarMap(){
        if (condition == null) {
            return new HashMap<>(accountBar);
        } else {
            if (!condition.isEnableFix() || condition.isEnableFree()) {
                if (condition.isSave()) return conditionBar.getOrDefault(condition.getKey(), new HashMap<>());
                else return new HashMap<>(tempBar);
            } else return new HashMap<>(condition.getFixMap());
        }
    }

    public boolean setBarMap(Map<Integer, String> map){
        if (condition == null) {
            accountBar.clear();
            map.keySet().removeIf(v->v > Setting.getInstance().getBarMaxLine() * 9 + 8);
            accountBar.putAll(map);
            return false;
        } else {
            int size = map.size();
            boolean flag = false;
            if (condition.isEnableFix()) {
                if (!condition.isEnableFree()) {
                    map.keySet().removeIf(v->v>condition.getBarSize() * 9 + 8);
                    if (!condition.getFreeList().contains(-1)) {
                        map.keySet().removeIf(v->!condition.getFreeList().contains(v));
                        flag = map.size() != size;
                        size = map.size();
                        condition.getFixMap().forEach(map::putIfAbsent);
                        flag = flag || map.size() != size;
                    }
                    if (condition.isSave()) conditionBar.put(condition.getKey(), map);
                    else {
                        tempBar.clear();
                        tempBar.putAll(map);
                    }
                } else {
                    flag = size != condition.getFixMap().size();
                    for (Map.Entry<Integer, String> entry : map.entrySet()) {
                        flag = flag || condition.getFixMap().getOrDefault(entry.getKey(), "").equals(entry.getValue());
                    }
                }
            } else {
                if (condition.isSave()) conditionBar.put(condition.getKey(), map);
                else {
                    tempBar.clear();
                    tempBar.putAll(map);
                }
            }
            return flag;
        }
    }

    public Save getSave(){
        return new Save(accountBar, conditionBar);
    }

    public void setSave(Save save){
        this.accountBar.clear();
        this.conditionBar.clear();
        if (save.a != null) this.accountBar.putAll(save.a);
        if (save.c != null) this.conditionBar.putAll(save.c);
    }

    public void clean(){
        tempBar.clear();
        condition = null;
        conditionBar.clear();
        accountBar.clear();
    }

    static class Save{
        private final Map<Integer, String> a;
        private final Map<String, Map<Integer, String>> c;
        Save(Map<Integer, String> a, Map<String, Map<Integer, String>> c){
            this.a = a;
            this.c = c;
        }
    }
}
