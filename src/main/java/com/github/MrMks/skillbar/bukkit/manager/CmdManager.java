package com.github.MrMks.skillbar.bukkit.manager;

import com.github.MrMks.skillbar.bukkit.LogicHandler;
import com.github.MrMks.skillbar.bukkit.cmd.*;
import com.rit.sucy.commands.CommandManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.SenderType;
import org.bukkit.plugin.java.JavaPlugin;

public class CmdManager {

    private final JavaPlugin plugin;
    public CmdManager(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void init(ClientManager manager, LogicHandler handler){
        ConfigurableCommand root = new ConfigurableCommand(plugin, "bar", SenderType.ANYONE);
        ConfigurableCommand version = new ConfigurableCommand(plugin, "version", SenderType.ANYONE, new CmdVersion(), "Check plugin version", "", "skillbar.version");
        //version.setAliases(ImmutableList.of("v"));
        ConfigurableCommand ban = new ConfigurableCommand(plugin, "ban", SenderType.ANYONE,new CmdBan(manager, handler),"ban a player from Skill Bar", "<player>", "skillbar.ban");
        ConfigurableCommand un_ban = new ConfigurableCommand(plugin, "unban", SenderType.ANYONE,new CmdUnban(manager, handler),"unban a player from Skill Bar", "<player>", "skillbar.ban");
        ConfigurableCommand reload = new ConfigurableCommand(plugin, "reload", SenderType.ANYONE,new CmdReload(), "reload the skillbar plugin", "","skillbar.reload");
        ConfigurableCommand info = new ConfigurableCommand(plugin,"info", SenderType.PLAYER_ONLY, new CmdInfo(manager), "show skill bar info for user", "", "skillbar.info");
        ConfigurableCommand fBan = new ConfigurableCommand(plugin, "forceban", SenderType.ANYONE, new CmdForceBan(), "add a player name to black list without any check", "<player>","skillbar.forceban");
        ConfigurableCommand fUnban = new ConfigurableCommand(plugin, "forceunban", SenderType.ANYONE, new CmdForceUnban(), "remove a player name from black list without any check", "<player>","skillbar.forceban");
        root.addSubCommands(ban, un_ban, reload, info, fBan, fUnban, version);

        CommandManager.registerCommand(root);
    }

    public void unload(){
        CommandManager.unregisterCommands(plugin);
    }
}
