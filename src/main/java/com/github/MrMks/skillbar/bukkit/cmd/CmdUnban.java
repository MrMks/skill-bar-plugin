package com.github.MrMks.skillbar.bukkit.cmd;

import com.github.MrMks.skillbar.bukkit.LogicHandler;
import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CmdUnban implements IFunction {
    private final ClientManager manager;
    private final LogicHandler handler;

    public CmdUnban(ClientManager manager, LogicHandler handler){
        this.manager = manager;
        this.handler = handler;
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
                ClientData data = manager.get(player);
                if (data == null) {
                    commandSender.sendMessage("player data doesn't exist");
                } else {
                    if (data.getStatus().isBlocked()) {
                        data.getStatus().unblock();
                        if (data.getStatus().isDiscovered()) handler.onDiscover(data);
                        commandSender.sendMessage("player " + strings[0] + " has been un-banned");
                    } else {
                        commandSender.sendMessage("player " + strings[0] + " is not banned before");
                    }
                }
            }
        }
    }
}
