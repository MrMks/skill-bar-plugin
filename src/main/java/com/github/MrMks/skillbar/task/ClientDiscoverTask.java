package com.github.MrMks.skillbar.task;

import com.github.MrMks.skillbar.data.ClientStatus;
import com.github.MrMks.skillbar.pkg.PackageSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientDiscoverTask extends RepeatTask {
    private final HashMap<ClientStatus, Integer> list = new HashMap<>();
    private final PackageSender ps;
    public ClientDiscoverTask(PackageSender sender){
        super(5 * 1000,5 * 1000);
        this.ps = sender;
    }

    @Override
    protected void runTask() {
        synchronized (list){
            if (list.isEmpty()) return;
            ArrayList<ClientStatus> re = new ArrayList<>();
            for (ClientStatus m : list.keySet()){
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
            for (ClientStatus m : re) {
                list.remove(m);
                if (!m.isDiscovered()) m.disable();
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    public void addName(ClientStatus m){
        synchronized (list) {
            if (!list.containsKey(m)) list.put(m,0);
        }
    }

    public void removeName(ClientStatus m){
        synchronized (list) {
            list.remove(m);
        }
    }
}