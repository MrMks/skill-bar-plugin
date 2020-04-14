package com.github.MrMks.skillbar.bukkit.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CmdReload implements IFunction {
    @Override
    public void execute(ConfigurableCommand configurableCommand, Plugin plugin, CommandSender commandSender, String[] strings) {
        plugin.onDisable();
        plugin.onEnable();
        plugin.getLogger().info("Plugin reloaded");
        commandSender.sendMessage("§2Plugin reloaded");
    }
}
