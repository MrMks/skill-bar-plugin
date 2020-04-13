package com.github.MrMks.skillbar;

import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.data.ClientBar;
import com.github.MrMks.skillbar.data.ClientData;
import com.github.MrMks.skillbar.data.ClientManager;
import com.github.MrMks.skillbar.pkg.PackageListener;
import com.github.MrMks.skillbar.pkg.PluginSender;
import com.github.MrMks.skillbar.task.AutoSaveTask;
import com.github.MrMks.skillbar.task.ClientDiscoverTask;
import com.github.MrMks.skillbar.task.CoolDownTask;
import com.github.MrMks.skillbar.task.LoopThread;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@SuppressWarnings("unused")
public class SkillBar extends JavaPlugin implements Listener {
    private LoopThread task;
    private ClientManager manager;

    @Override
    public void onLoad() {
        super.onLoad();
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File file = new File(getDataFolder(), "player");
        if (!file.exists()) file.mkdir();
        ClientBar.setPath(getDataFolder());
        if (!new File(getDataFolder(), "config.yml").exists()) this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Setting.getInstance().readConfig(getConfig());
        BlackList.init(getDataFolder());
        PluginSender.init(this);

        manager = new ClientManager();
        task = new LoopThread();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Constants.CHANNEL_NAME);
        Bukkit.getMessenger().registerIncomingPluginChannel(this,Constants.CHANNEL_NAME,new PackageListener(manager));

        ClientDiscoverTask cdt = new ClientDiscoverTask();

        HandlerList.unregisterAll((Plugin) this);
        Bukkit.getPluginManager().registerEvents(new MainListener(this, manager, cdt), this);

        CmdManager.init(this, manager);

        task.addTask(new CoolDownTask(manager));
        task.addTask(cdt);
        task.addTask(new AutoSaveTask());
        for (ClientData data : manager.getAll()) data.getEventHandler().enable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        CmdManager.unload(this);
        if (task != null) task.disable();
        task = null;
        if (isEnabled()) {
            for (ClientData data : manager.getAll()) data.getEventHandler().disable();
        }
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
        PluginSender.clean();
        if (manager != null) manager.clearSaveAll();
        BlackList.saveUnload();
        manager = null;
        reloadConfig();
    }
}