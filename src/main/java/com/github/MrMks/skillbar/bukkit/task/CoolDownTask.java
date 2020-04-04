package com.github.MrMks.skillbar.bukkit.task;

import com.github.MrMks.skillbar.bukkit.manager.ClientStatus;
import com.github.MrMks.skillbar.bukkit.manager.PlayerManager;
import com.github.MrMks.skillbar.bukkit.pkg.PackageSender;
import com.rit.sucy.version.VersionManager;
import org.bukkit.entity.Player;

public class CoolDownTask extends RepeatTask {
    private PackageSender p;
    public CoolDownTask(PackageSender ps){
        super(500,500);
        this.p = ps;
    }

    @Override
    public void runTask() {
        Player[] players = VersionManager.getOnlinePlayers();
        for (Player player : players){
            if (PlayerManager.get(player).getStatus() == ClientStatus.Enabled){
                p.sendCoolDown(player);
            }
        }
    }

    @Override
    public boolean isFinish() {
        return false;
    }
}
