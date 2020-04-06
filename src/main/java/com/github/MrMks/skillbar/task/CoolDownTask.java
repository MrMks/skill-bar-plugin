package com.github.MrMks.skillbar.task;

import com.github.MrMks.skillbar.data.ClientStatus;
import com.github.MrMks.skillbar.data.Manager;
import com.github.MrMks.skillbar.pkg.PackageSender;
import com.rit.sucy.version.VersionManager;
import org.bukkit.entity.Player;

public class CoolDownTask extends RepeatTask {
    private PackageSender p;
    private Manager m;
    public CoolDownTask(PackageSender ps, Manager manager){
        super(500,500);
        this.p = ps;
        this.m = manager;
    }

    @Override
    public void runTask() {
        Player[] players = VersionManager.getOnlinePlayers();
        for (Player player : players){
            if (m.get(player) != null && m.get(player).getStatus() == ClientStatus.Enabled){
                p.sendCoolDown(player);
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }
}