package com.github.MrMks.skillbar.bukkit.cmd;

import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CmdVersion implements IFunction {
    @Override
    public void execute(ConfigurableCommand configurableCommand, Plugin plugin, CommandSender commandSender, String[] strings) {
        commandSender.sendMessage(plugin.getDescription().getVersion());
    }
}
