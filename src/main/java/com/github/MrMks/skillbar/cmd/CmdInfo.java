package com.github.MrMks.skillbar.cmd;

import com.github.MrMks.skillbar.data.ClientData;
import com.github.MrMks.skillbar.data.ClientStatus;
import com.github.MrMks.skillbar.data.Manager;
import com.rit.sucy.commands.ConfigurableCommand;
import com.rit.sucy.commands.IFunction;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CmdInfo implements IFunction {

    private Manager manager;
    public CmdInfo(Manager manager){
        this.manager = manager;
    }

    @Override
    public void execute(ConfigurableCommand configurableCommand, Plugin plugin, CommandSender commandSender, String[] strings) {
        if (commandSender instanceof Player) {
            if (SkillAPI.isLoaded()){
                Player player = (Player) commandSender;
                ClientData data = manager.get(player);
                boolean hasPlayer = SkillAPI.hasPlayerData(player);
                PlayerData pData = SkillAPI.getPlayerData(player);
                boolean passCheck = hasPlayer && pData.getClasses().size() > 0 && pData.getSkills().size() > 0;
                boolean block = data.isBlocked();
                boolean enable = data.getStatus() == ClientStatus.Enabled;

                String builder = "§2Skill Bar info:\n" +
                        "§6=====\n§r" +
                        "Have data in SkillAPI: " + format(hasPlayer) + "\n" +
                        "Valid check about skills: " + format(passCheck) + "\n" +
                        "Not banned for reasons: " + format(!block) + "\n" +
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
