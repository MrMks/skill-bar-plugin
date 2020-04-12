package com.github.MrMks.skillbar.cmd;

import com.github.MrMks.skillbar.data.ClientStatus;
import com.github.MrMks.skillbar.data.ClientManager;
import com.github.MrMks.skillbar.pkg.PackageSender;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CmdUnban implements IFunction {
    private ClientManager manager;
    private PackageSender sender;
    public CmdUnban(ClientManager manager, PackageSender sender){
        this.manager = manager;
        this.sender = sender;
    }

    @Override
    public void execute(ConfigurableCommand configurableCommand, Plugin plugin, CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
            configurableCommand.displayHelp(commandSender);
        } else {
            Player player = Bukkit.getPlayer(strings[0]);
            if (player == null) {
                commandSender.sendMessage("player doesn't exist");
            } else {
                ClientStatus data = manager.get(player);
                if (data == null) {
                    commandSender.sendMessage("player data doesn't exist");
                } else {
                    if (data.isBlocked()) {
                        data.unblock();
                        sender.sendDiscover(player);
                        commandSender.sendMessage("player " + strings[0] + " has been un-banned");
                    } else {
                        commandSender.sendMessage("player " + strings[0] + " is not banned before");
                    }
                }
            }
        }
    }
}
