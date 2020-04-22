package com.github.MrMks.skillbar.bukkit.cmd;

import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.bukkit.data.IClientStatus;
import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CmdInfo implements IFunction {

    private final ClientManager manager;
    public CmdInfo(ClientManager manager){
        this.manager = manager;
    }

    @Override
    public void execute(ConfigurableCommand configurableCommand, Plugin plugin, CommandSender commandSender, String[] strings) {
        if (commandSender instanceof Player) {
            if (SkillAPI.isLoaded()){
                Player player = (Player) commandSender;
                ClientData data = manager.get(player);
                IClientStatus status = data.getStatus();
                boolean hasPlayer = SkillAPI.hasPlayerData(player);
                PlayerData pData = SkillAPI.getPlayerData(player);
                boolean passCheck = hasPlayer && pData.getClasses().size() > 0 && pData.getSkills().size() > 0;
                boolean world = SkillAPI.getSettings().isWorldEnabled(player.getWorld());
                boolean block = status.isBlocked();
                boolean discovered = status.isDiscovered();
                boolean enable = status.isEnabled();

                String builder = "§2Skill Bar info:\n" +
                        "§6=====\n§r" +
                        "Have data in SkillAPI: " + format(hasPlayer) + "\n" +
                        "Valid check about skills: " + format(passCheck) + "\n" +
                        "World check: " + format(world) + "\n" +
                        "Not banned for reasons: " + format(!block) + "\n" +
                        "Client discovered: " + format(discovered) + "\n" +
                        "Enabled on Server: " + format(enable) + "\n" +
                        "§6=====";
                commandSender.sendMessage(builder);

            } else {
                commandSender.sendMessage("SkillAPI is not loaded");
            }
        }
    }

    private String format(boolean flag){
        return "§" + (flag ? "2" : "c") + flag + "§r";
    }
}
