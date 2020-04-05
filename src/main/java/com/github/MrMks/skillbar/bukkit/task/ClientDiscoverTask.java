package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.bukkit.pkg.PackageSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientDiscoverTask extends RepeatTask {
    private final HashMap<ClientData, Integer> list = new HashMap<>();
    private final PackageSender ps;
    public ClientDiscoverTask(PackageSender sender){
        super(5 * 1000,5 * 1000);
        this.ps = sender;
    }

    @Override
    protected void runTask() {
        synchronized (list){
            if (list.isEmpty()) return;
            ArrayList<ClientData> re = new ArrayList<>();
            for (ClientData m : list.keySet()){
                if (!m.isDiscovered()) {
                    int times = list.get(m);
                    if (times > 5) {
                        re.add(m);
                        continue;
                    }
                    Player player = Bukkit.getPlayer(m.getUid());
                    ps.sendDiscover(player);
                    list.put(m, times + 1);
                } else {
                    re.add(m);
                }
            }
            for (ClientData m : re) {
                list.remove(m);
                if (!m.isDiscovered()) m.disable();
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    public void addName(ClientData m){
        synchronized (list) {
            if (!list.containsKey(m)) list.put(m,0);
        }
    }

    public void removeName(ClientData m){
        synchronized (list) {
            list.remove(m);
        }
    }
}
