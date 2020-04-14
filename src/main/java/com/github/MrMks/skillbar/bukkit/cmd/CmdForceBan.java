package com.github.MrMks.skillbar.bukkit.cmd;

import com.github.MrMks.skillbar.bukkit.BlackList;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CmdForceBan implements IFunction {
    @Override
    public void execute(ConfigurableCommand configurableCommand, Plugin plugin, CommandSender commandSender, String[] strings) {
        if (strings.length == 0) configurableCommand.displayHelp(commandSender);
        else {
            BlackList.getInstance().add(strings[0]);
            commandSender.sendMessage("Added " + strings[0] + " to black list");
        }
    }
}
