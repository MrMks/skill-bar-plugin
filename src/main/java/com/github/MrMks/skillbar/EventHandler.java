package com.github.MrMks.skillbar;

import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.github.MrMks.skillbar.data.ClientBar;
import com.github.MrMks.skillbar.data.ClientStatus;
import com.github.MrMks.skillbar.data.EnumStatus;
import com.github.MrMks.skillbar.pkg.BukkitByteBuilder;
import com.github.MrMks.skillbar.pkg.BukkitSkillInfo;
import com.github.MrMks.skillbar.pkg.PluginSender;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventHandler {
    private final UUID uuid;
    private final ClientStatus status;
    private final ClientBar bar;
    private final PluginSender sender;
    public EventHandler(UUID uuid, ClientStatus status, ClientBar bar, PluginSender sender){
        this.uuid = uuid;
        this.status = status;
        this.bar = bar;
        this.sender = sender;
    }

    public void onJoin(){
        if (!status.isDiscovered()) {
            sender.send(SPackage.BUILDER.buildDiscover(BukkitByteBuilder::new, Constants.VERSION));
        }
    }

    public void onResetProfess(){
        if (status.isDiscovered() && status.getStatus() == EnumStatus.Enabled) {
            int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
            sender.send(SPackage.BUILDER.buildCleanUp(BukkitByteBuilder::new, active));
            status.disable();
            sender.send(SPackage.BUILDER.buildDisable(BukkitByteBuilder::new));
            bar.setBar(active, Collections.emptyMap());
        }
    }

    public void onStartProfess(){
        if (status.isDiscovered() && status.getStatus() != EnumStatus.Enabled) {
            int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
            int size = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid)).getSkills().size();
            status.enable();
            sender.send(SPackage.BUILDER.buildEnable(BukkitByteBuilder::new, active, size));
        }
    }
    public void onChangeProfess(){
        if (status.isDiscovered() && status.getStatus() == EnumStatus.Enabled) {
            int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
            int size = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid)).getSkills().size();
            sender.send(SPackage.BUILDER.buildAddSkill(BukkitByteBuilder::new, active, size));
        }
    }

    public void onAccountSwitch(){
        if (status.isDiscovered() && status.getStatus() == EnumStatus.Enabled) {
            int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
            int size = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid)).getSkills().size();
            sender.send(SPackage.BUILDER.buildAccount(BukkitByteBuilder::new,active,size));
        }
    }
    public void onAccToEnable(){
        enable();
    }
    public void onAccToDisable(){
        disable();
    }

    public void onWorldToEnable(){
        enable();
    }
    public void onWorldToDisable(){
        disable();
    }

    public void onUpdateSkillInfo(String key){
        PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
        SkillInfo info;
        if (data.hasSkill(key)) {
            info = new BukkitSkillInfo(data.getSkill(key));
        } else info = SkillInfo.Empty;
        sender.send(SPackage.BUILDER.buildUpdateSkill(BukkitByteBuilder::new,info));
    }
    public void onUpdateCoolDownInfo(){
        PlayerAccounts accounts = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid));
        PlayerData data = accounts.getActiveData();
        Map<String, Integer> map = new HashMap<>();
        for (int index : bar.keys()) {
            if (data.hasSkill(bar.getSkill(index))) map.put(bar.getSkill(index), data.getSkill(bar.getSkill(index)).getCooldown());
        }
        sender.send(SPackage.BUILDER.buildCoolDown(BukkitByteBuilder::new, map));
    }

    public void enable(){
        if (status.isDiscovered() && status.getStatus() != EnumStatus.Enabled) {
            int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
            int size = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid)).getSkills().size();
            status.enable();
            sender.send(SPackage.BUILDER.buildEnable(BukkitByteBuilder::new, active, size));
        }
    }
    public void disable(){
        if (status.isDiscovered() && status.getStatus() == EnumStatus.Enabled) {
            status.disable();
            sender.send(SPackage.BUILDER.buildDisable(BukkitByteBuilder::new));
        }
    }
}
