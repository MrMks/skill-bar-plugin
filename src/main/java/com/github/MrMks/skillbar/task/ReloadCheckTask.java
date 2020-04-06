package com.github.MrMks.skillbar.task;

import com.sucy.skill.SkillAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReloadCheckTask implements Runnable {
    private Plugin pl;
    private CommandSender sender;
    public ReloadCheckTask(Plugin plugin, CommandSender sender){
        this.pl = plugin;
        this.sender = sender;
    }

    @Override
    public void run() {
        if (SkillAPI.isLoaded()){
            pl.onEnable();
            sender.sendMessage("§2Skill Bar plugin has been reloaded, too");
        } else {
            Bukkit.getScheduler().runTaskLater(pl, this, 20);
        }
    }
}