package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.manager.ClientStatus;
import com.github.MrMks.skillbar.bukkit.manager.Manager;
import com.github.MrMks.skillbar.bukkit.pkg.PackageSender;
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
            if (m.get(player).getStatus() == ClientStatus.Enabled){
                p.sendCoolDown(player);
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }
}
