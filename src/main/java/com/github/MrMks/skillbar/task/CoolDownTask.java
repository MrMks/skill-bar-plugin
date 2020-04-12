package com.github.MrMks.skillbar.task;

import com.github.MrMks.skillbar.data.EnumStatus;
import com.github.MrMks.skillbar.data.ClientManager;
import com.github.MrMks.skillbar.pkg.PackageSender;
import com.rit.sucy.version.VersionManager;
import org.bukkit.entity.Player;

public class CoolDownTask extends RepeatTask {
    private PackageSender p;
    private ClientManager m;
    public CoolDownTask(PackageSender ps, ClientManager manager){
        super(500,500);
        this.p = ps;
        this.m = manager;
    }

    @Override
    public void runTask() {
        Player[] players = VersionManager.getOnlinePlayers();
        for (Player player : players){
            if (m.get(player) != null && m.get(player).getStatus() == EnumStatus.Enabled){
                p.sendCoolDown(player);
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }
}