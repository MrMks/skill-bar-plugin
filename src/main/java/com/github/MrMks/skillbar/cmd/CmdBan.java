package com.github.MrMks.skillbar.cmd;

import com.github.MrMks.skillbar.data.ClientData;
import com.github.MrMks.skillbar.data.Manager;
import com.github.MrMks.skillbar.pkg.PackageSender;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CmdBan implements IFunction {

    private Manager manager;
    private PackageSender sender;
    public CmdBan(Manager manager, PackageSender sender){
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
                //plugin.getLogger().info(player.getUniqueId().toString());
                ClientData data = manager.get(player);
                if (data == null) {
                    commandSender.sendMessage("player data doesn't exist");
                } else {
                    sender.sendDisable(player);
                    data.block();
                    commandSender.sendMessage("player " + strings[0] + " has been banned to use skill bar");
                }
            }
        }
    }
}
