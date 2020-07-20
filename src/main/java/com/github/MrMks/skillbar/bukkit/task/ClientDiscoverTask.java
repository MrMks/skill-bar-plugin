package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.LogicHandler;
import com.github.MrMks.skillbar.bukkit.data.ClientData;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientDiscoverTask extends RepeatTask {
    private final HashMap<ClientData, Integer> list = new HashMap<>();
    private final LogicHandler handler;
    public ClientDiscoverTask(LogicHandler handler){
        super(5 * 1000,5 * 1000);
        this.handler = handler;
    }

    @Override
    protected void runTask() {
        synchronized (list){
            if (list.isEmpty()) return;
            ArrayList<ClientData> re = new ArrayList<>();
            for (ClientData data : list.keySet()){
                if (!data.getStatus().isDiscovered()) {
                    int times = list.get(data);
                    if (times > 5) {
                        re.add(data);
                        continue;
                    }
                    handler.onJoin(data);
                    list.put(data, times + 1);
                } else {
                    re.add(data);
                }
            }
            for (ClientData data : re) {
                list.remove(data);
                if (!data.getStatus().isDiscovered()) data.getStatus().disable();
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    public void addName(ClientData data){
        synchronized (list) {
            if (!list.containsKey(data)) list.put(data,0);
        }
    }

    public void removeName(ClientData data){
        synchronized (list) {
            list.remove(data);
        }
    }
}