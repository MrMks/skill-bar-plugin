package com.github.MrMks.skillbar.cmd;

import com.github.MrMks.skillbar.BlackList;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CmdForceUnban implements IFunction {

    @Override
    public void execute(ConfigurableCommand configurableCommand, Plugin plugin, CommandSender commandSender, String[] strings) {
        if (strings.length == 0) configurableCommand.displayHelp(commandSender);
        else {
            BlackList.getInstance().remove(strings[0]);
            commandSender.sendMessage("Removed " + strings[0] + " from black list");
        }

    }
}
