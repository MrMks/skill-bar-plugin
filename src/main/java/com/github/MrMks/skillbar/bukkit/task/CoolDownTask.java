package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.pkg.PackageSender;
import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import com.github.MrMks.skillbar.bukkit.manager.ClientStatus;
import com.rit.sucy.version.VersionManager;
import org.bukkit.entity.Player;

public class CoolDownTask extends RepeatTask {
    private ClientManager c;
    private PackageSender p;
    public CoolDownTask(ClientManager cm, PackageSender ps){
        super(500,500);
        this.c = cm;
        this.p = ps;
    }

    @Override
    public void runTask() {
        Player[] players = VersionManager.getOnlinePlayers();
        for (Player player : players){
            if (c.getClientStatus(player.getName()) != ClientStatus.Request_Disable){
                p.sendCoolDown(player);
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }
}
