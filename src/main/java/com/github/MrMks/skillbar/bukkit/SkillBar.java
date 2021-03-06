package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.data.ClientAccounts;
import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import com.github.MrMks.skillbar.bukkit.manager.CmdManager;
import com.github.MrMks.skillbar.bukkit.manager.ConditionManager;
import com.github.MrMks.skillbar.bukkit.pkg.BukkitByteBuilder;
import com.github.MrMks.skillbar.bukkit.pkg.PackageListener;
import com.github.MrMks.skillbar.bukkit.pkg.MessageSender;
import com.github.MrMks.skillbar.bukkit.task.AutoSaveTask;
import com.github.MrMks.skillbar.bukkit.task.ClientDiscoverTask;
import com.github.MrMks.skillbar.bukkit.task.CoolDownTask;
import com.github.MrMks.skillbar.bukkit.task.LoopThread;
import com.github.MrMks.skillbar.bukkit.utils.LogUtil;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.pkg.SPackage;
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
    private LogicHandler logicHandler;
    private CmdManager cmd;

    @Override
    public void onLoad() {
        super.onLoad();
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File file = new File(getDataFolder(), "player");
        if (!file.exists()) file.mkdir();
        ClientAccounts.setPath(getDataFolder());
        if (!new File(getDataFolder(), "config.yml").exists()) this.saveDefaultConfig();
        if (!new File(getDataFolder(), "conditions.yml").exists()) this.saveDefaultConfig();
        LogUtil.setLogger(getLogger());
    }

    @Override
    public void onEnable() {
        // create manager && loopTask
        manager = new ClientManager();
        logicHandler = new LogicHandler(new MessageSender(this));
        ClientDiscoverTask cdt = new ClientDiscoverTask(logicHandler);

        // static class init
        Setting.getInstance().readConfig(getConfig());
        BlackList.init(getDataFolder());
        ConditionManager.init(new FileConfigStore(this, "conditions.yml"), getLogger());

        // register events
        HandlerList.unregisterAll((Plugin) this);
        Bukkit.getPluginManager().registerEvents(new MainListener(this, manager, cdt, logicHandler), this);

        // register channels
        SPackage.BUILDER.init(BukkitByteBuilder::new);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, Constants.CHANNEL_NAME);
        Bukkit.getMessenger().registerIncomingPluginChannel(this,Constants.CHANNEL_NAME,new PackageListener(manager, logicHandler));

        // task adds
        task = new LoopThread();
        task.addTask(new CoolDownTask(manager, logicHandler));
        task.addTask(cdt);
        task.addTask(new AutoSaveTask());
        task.enable();

        // discover all players
        for (Player player : VersionManager.getOnlinePlayers()){
            if (player != null) logicHandler.onJoin(manager.generate(player));
        }

        // register cmd
        if (cmd != null) cmd.unload();
        cmd = new CmdManager(this);
        cmd.init(manager, logicHandler);
    }

    @Override
    public void onDisable() {
        // unregister cmd
        if (cmd != null) {
            cmd.unload();
            cmd = null;
        }

        // stop task loop
        if (task != null) task.disable();
        task = null;

        // disable all clients && clean client data
        if (manager != null) {
            if (isEnabled()) {
                manager.getAll().forEach(data->logicHandler.onPluginDisable(data));
            }
            manager.clearSaveAll();
            manager = null;
            logicHandler = null;
        }

        // unregister channels
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
        SPackage.BUILDER.init(null);

        // clean static classes
        BlackList.saveUnload();
        ConditionManager.clean();
        reloadConfig();
    }
}