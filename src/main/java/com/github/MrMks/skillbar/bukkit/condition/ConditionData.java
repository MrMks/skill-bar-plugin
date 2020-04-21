package com.github.MrMks.skillbar.bukkit.condition;

import com.github.MrMks.skillbar.bukkit.data.ClientBar;

import java.util.*;

public class ConditionData {
    private Condition condition;
    private Map<Integer, String> bar;
    private final ClientBar saveBar;

    public ConditionData(ClientBar bar){
        this.saveBar = bar;
    }

    public void setCondition(Condition condition) {
        if (condition != null) {
            this.condition = condition;
            if (condition.isSave()) if (!saveBar.hasConditionBar(condition.getKey())) saveBar.setConditionBar(condition.getKey(), new HashMap<>(condition.getFixMap()));
            else if (condition.isEnableFree() || !condition.isEnableFix()) bar = new HashMap<>(condition.getFixMap());
        }
    }

    public void setBar(Map<Integer, String> map){
        if (condition != null) {
            if (condition.isEnableFree() || !condition.isEnableFix()) {
                if (condition.isSave()) saveBar.setConditionBar(condition.getKey(), map);
                else bar = map;
            }
        }
    }


    public Optional<Condition> getCondition(){
        return Optional.ofNullable(condition);
    }

    public Map<Integer, String> getConditionBar(){
        if (condition == null) return Collections.emptyMap();
        else {
            if (condition.isEnableFree() || !condition.isEnableFix()) {
                if (condition.isSave()) return saveBar.getConditionBar(condition.getKey());
                else return bar == null ? Collections.emptyMap() : bar;
            } else {
                return condition.getFixMap();
            }
        }
    }

    public void leaveCondition(){
        condition = null;
        bar = null;
    }
}
