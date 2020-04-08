package com.github.MrMks.skillbar;

import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.data.Manager;
import com.github.MrMks.skillbar.data.PlayerBar;
import com.github.MrMks.skillbar.pkg.PackageListener;
import com.github.MrMks.skillbar.pkg.PackageSender;
import com.github.MrMks.skillbar.task.ClientDiscoverTask;
import com.github.MrMks.skillbar.task.CoolDownTask;
import com.github.MrMks.skillbar.task.LoopThread;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkillBar extends JavaPlugin implements Listener {
    private LoopThread task;
    private PackageSender sender;
    private Manager manager;

    @Override
    public void onLoad() {
        super.onLoad();
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File file = new File(getDataFolder(), "player");
        if (!file.exists()) file.mkdir();
        PlayerBar.setPath(getDataFolder());
        if (!new File(getDataFolder(), "config.yml").exists()) this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Setting.getInstance().readConfig(getConfig());

        manager = new Manager();
        sender = new PackageSender(this, manager);
        task = new LoopThread();
        if (!sender.isLoad()) {
            getLogger().severe("Can't find method to use, plugin will disable");
            getPluginLoader().disablePlugin(this);
            return;
        }

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Constants.CHANNEL_NAME);
        Bukkit.getMessenger().registerIncomingPluginChannel(this,Constants.CHANNEL_NAME,new PackageListener(this, sender, manager));

        ClientDiscoverTask cdt = new ClientDiscoverTask(sender);

        HandlerList.unregisterAll((Plugin) this);
        Bukkit.getPluginManager().registerEvents(new MainListener(this, sender, manager, cdt), this);

        task.addTask(new CoolDownTask(sender, manager));
        task.addTask(cdt);
        sender.sendAllDiscover();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) task.disable();
        task = null;
        if (isEnabled()) sender.sendAllDisable();
        sender = null;
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
        if (manager != null) manager.clearSaveAll();
        manager = null;
        reloadConfig();
    }
}