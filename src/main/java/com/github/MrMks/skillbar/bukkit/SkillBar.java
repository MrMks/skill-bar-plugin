package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import com.github.MrMks.skillbar.bukkit.pkg.PackageListener;
import com.github.MrMks.skillbar.bukkit.pkg.PackageSender;
import com.github.MrMks.skillbar.bukkit.task.ClientDiscoverTask;
import com.github.MrMks.skillbar.bukkit.task.CoolDownTask;
import com.github.MrMks.skillbar.bukkit.task.LoopThread;
import com.github.MrMks.skillbar.common.Constants;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkillBar extends JavaPlugin implements Listener {
    private LoopThread task;
    private PackageSender sender;

    @Override
    public void onLoad() {
        super.onLoad();
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File file = new File(getDataFolder(), "player");
        if (!file.exists()) file.mkdir();
        PlayerBar.setPath(getDataFolder());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ClientManager cm = new ClientManager();
        sender = new PackageSender(this,cm);
        task = new LoopThread();
        if (!sender.isLoad()) {
            getPluginLoader().disablePlugin(this);
            return;
        }

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Constants.CHANNEL_NAME);
        Bukkit.getMessenger().registerIncomingPluginChannel(this,Constants.CHANNEL_NAME,new PackageListener(this, sender, cm));

        ClientDiscoverTask cdt = new ClientDiscoverTask(cm,sender);

        HandlerList.unregisterAll((Plugin) this);
        Bukkit.getPluginManager().registerEvents(new MainListener(this, sender, cm, cdt), this);

        task.addTask(new CoolDownTask(cm,sender));
        task.addTask(cdt);
        sender.sendAllEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        task.disable();
        task = null;
        if (isEnabled()) sender.sendAllDisable();
        sender = null;
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
        PlayerBar.unloadSaveAll();
    }
}
