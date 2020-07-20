package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.LogicHandler;
import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import com.rit.sucy.version.VersionManager;
import org.bukkit.entity.Player;

public class CoolDownTask extends RepeatTask {
    private final ClientManager m;
    private final LogicHandler handler;
    public CoolDownTask(ClientManager manager, LogicHandler handler){
        super(500,500);
        this.m = manager;
        this.handler = handler;
    }

    @Override
    public void runTask() {
        Player[] players = VersionManager.getOnlinePlayers();
        for (Player player : players){
            ClientData data = m.get(player);
            if (data != null && data.getStatus().isEnabled()){
                handler.onUpdateCoolDownInfo(data);
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }
}