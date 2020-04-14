package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.manager.ConditionManager;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.bukkit.data.ClientBar;
import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.bukkit.data.ClientManager;
import com.github.MrMks.skillbar.bukkit.manager.CmdManager;
import com.github.MrMks.skillbar.bukkit.pkg.PackageListener;
import com.github.MrMks.skillbar.bukkit.pkg.PluginSender;
import com.github.MrMks.skillbar.bukkit.task.AutoSaveTask;
import com.github.MrMks.skillbar.bukkit.task.ClientDiscoverTask;
import com.github.MrMks.skillbar.bukkit.task.CoolDownTask;
import com.github.MrMks.skillbar.bukkit.task.LoopThread;
import com.rit.sucy.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        if (!new File(getDataFolder(), "conditions.yml").exists()) this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        // create manager && loopTask
        manager = new ClientManager();
        task = new LoopThread();
        ClientDiscoverTask cdt = new ClientDiscoverTask();

        // static class init
        Setting.getInstance().readConfig(getConfig());
        BlackList.init(getDataFolder());
        PluginSender.init(this);
        ConditionManager.init(new FileConfigStore(this, "conditions.yml"));

        // register events
        HandlerList.unregisterAll((Plugin) this);
        Bukkit.getPluginManager().registerEvents(new MainListener(this, manager, cdt), this);

        // register channels
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Constants.CHANNEL_NAME);
        Bukkit.getMessenger().registerIncomingPluginChannel(this,Constants.CHANNEL_NAME,new PackageListener(manager));

        // task adds
        task.addTask(new CoolDownTask(manager));
        task.addTask(cdt);
        task.addTask(new AutoSaveTask());
        task.enable();

        // discover all players
        for (Player player : VersionManager.getOnlinePlayers()){
            manager.prepare(player);
            manager.get(player).getEventHandler().onJoin();
        }

        // register cmd
        CmdManager.init(this, manager);
    }

    @Override
    public void onDisable() {
        // unregister cmd
        CmdManager.unload(this);

        // stop task loop
        if (task != null) task.disable();
        task = null;

        // disable all clients && clean client data
        if (manager != null) {
            if (isEnabled()) {
                for (ClientData data : manager.getAll()) data.getEventHandler().disable();
            }
            manager.clearSaveAll();
            manager = null;
        }

        // unregister channels
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);

        // clean static classes
        PluginSender.clean();
        BlackList.saveUnload();
        ConditionManager.clean();
        reloadConfig();
    }
}