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

public class CmdBan implements IFunction {

    private final ClientManager manager;
    private final LogicHandler handler;

    public CmdBan(ClientManager manager, LogicHandler handler){
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
                //plugin.getLogger().info(player.getUniqueId().toString());
                ClientData data = manager.get(player);
                if (data == null) {
                    commandSender.sendMessage("player data doesn't exist");
                } else {
                    if (!data.getStatus().isBlocked()) {
                        if (data.getStatus().isDiscovered()) handler.sendDisable(data);
                        data.getStatus().block();
                    }
                    commandSender.sendMessage("player " + strings[0] + " has been banned to use skill bar");
                }
            }
        }
    }
}
