package com.github.MrMks.skillbar.bukkit.condition;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ConditionData {
    private Condition condition;
    private Map<Integer, String> bar;

    public void setCondition(Condition condition) {
        if (condition != null) {
            this.condition = condition;
        }
    }

    public void setBar(Map<Integer, String> map){
        if (condition != null) {
            Set<Integer> set = condition.getBarList().keySet();
            set.addAll(map.keySet());
            set.removeIf(v -> condition.getFreeList().contains(v));
            set.forEach(v->{
                if (condition.getBarList().containsKey(v)) map.put(v, condition.getBarList().get(v));
                else map.remove(v);
            });
            bar = map;
        }
    }

    public Optional<Condition> getCondition(){
        return Optional.ofNullable(condition);
    }

    public Map<Integer, String> getConditionBar(){
        return bar == null ? Collections.emptyMap() : bar;
    }

    public void leaveCondition(){
        condition = null;
        bar = null;
    }
}
