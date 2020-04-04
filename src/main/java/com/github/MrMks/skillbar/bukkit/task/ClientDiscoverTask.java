package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import com.github.MrMks.skillbar.bukkit.manager.ClientStatus;
import com.github.MrMks.skillbar.bukkit.pkg.PackageSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientDiscoverTask extends RepeatTask {
    private final HashMap<String, Integer> list = new HashMap<>();
    private final ClientManager cm;
    private final PackageSender ps;
    public ClientDiscoverTask(ClientManager manager, PackageSender sender){
        super(5 * 1000,5 * 1000);
        this.cm = manager;
        this.ps = sender;
    }

    @Override
    protected void runTask() {
        synchronized (list){
            if (list.isEmpty()) return;
            ArrayList<String> re = new ArrayList<>();
            for (String name : list.keySet()){
                if (cm.getClientStatus(name) == ClientStatus.Request_Enable) {
                    int times = list.get(name);
                    if (times > 5) {
                        re.add(name);
                        continue;
                    }
                    Player player = Bukkit.getPlayer(name);
                    ps.sendEnable(player);
                    list.put(name, times + 1);
                } else {
                    re.add(name);
                }
            }
            for (String name : re) {
                if (cm.getClientStatus(name) != ClientStatus.Enabled) cm.setClientStatus(name, ClientStatus.Request_Disable);
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    public void addName(String name){
        synchronized (list) {
            if (!list.containsKey(name)) list.put(name,0);
        }
    }

    public void removeName(String name){
        synchronized (list) {
            list.remove(name);
        }
    }
}
