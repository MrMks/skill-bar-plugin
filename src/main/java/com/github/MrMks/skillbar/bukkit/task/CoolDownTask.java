package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.bukkit.data.ClientManager;
import com.rit.sucy.version.VersionManager;
import org.bukkit.entity.Player;

public class CoolDownTask extends RepeatTask {
    private ClientManager m;
    public CoolDownTask(ClientManager manager){
        super(500,500);
        this.m = manager;
    }

    @Override
    public void runTask() {
        Player[] players = VersionManager.getOnlinePlayers();
        for (Player player : players){
            ClientData data = m.get(player);
            if (data != null && data.getStatus().isEnable()){
                data.getEventHandler().onUpdateCoolDownInfo();
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }
}