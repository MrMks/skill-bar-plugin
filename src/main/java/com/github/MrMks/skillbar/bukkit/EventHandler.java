package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.condition.Condition;
import com.github.MrMks.skillbar.bukkit.data.ClientAccount;
import com.github.MrMks.skillbar.bukkit.data.ClientAccounts;
import com.github.MrMks.skillbar.bukkit.data.ClientStatus;
import com.github.MrMks.skillbar.bukkit.pkg.BukkitSkillInfo;
import com.github.MrMks.skillbar.bukkit.pkg.PluginSender;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.Bukkit;

import java.util.*;

public class EventHandler {
    private final UUID uuid;
    private final ClientStatus status;
    private final PluginSender sender;
    private final ClientAccounts accounts;
    public EventHandler(UUID uuid, ClientStatus status, ClientAccounts accounts, PluginSender sender){
        this.uuid = uuid;
        this.status = status;
        this.sender = sender;
        this.accounts = accounts;
    }

    /**
     * player join, start discover
     * send if not discovered
     */
    public void onJoin(){
        if (!status.isDiscovered()) {
            sender.send(SPackage.BUILDER.buildDiscover());
        }
    }

    /**
     * player reset profess
     * send if discovered, not blocked, and enabled
     * will set status to disabled
     */
    public void onResetProfess(){
        if (status.isEnabled()) {
            int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
            status.cleanCache(active);
            sender.send(SPackage.BUILDER.buildCleanUp(active));
            sendDisable();
            accounts.getAccount(active).clean();
        }
    }

    /**
     * player profess a class from null
     * send if discovered, not blocked and not enabled 
     */
    public void onStartProfess(){
        if (status.isDiscovered() && !status.isBlocked() && !status.isEnabled()) {
            sendAccount();
            sendEnable();
        }
    }

    public void onChangeProfess(PlayerClass playerClass){
        if (status.isEnabled()) {
            List<Skill> skills = playerClass.getData().getSkills();
            List<SkillInfo> infoList = new ArrayList<>();
            PlayerData data = playerClass.getPlayerData();
            for (Skill skill : skills) {
                String skillKey = skill.getKey();
                if (data.hasSkill(skillKey)) infoList.add(new BukkitSkillInfo(data.getSkill(skillKey)));
            }
            sender.send(SPackage.BUILDER.buildAddSkill(infoList));
        }
    }

    public void onAccountSwitch(){
        if (status.isEnabled()) {
            sendAccount();
        }
    }

    public void onAccToEnable(){
        sendAccount();
        sendEnable();
    }

    public void onAccToDisable(){
        sendDisable();
        sendAccount();
    }

    public void onWorldToEnable(){
        sendAccount();
        sendEnable();
    }
    public void onWorldToDisable(){
        sendDisable();
    }

    public void onUpdateSkillInfo(String key){
        if (status.isDiscovered() && !status.isBlocked()) {
            PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
            SkillInfo info;
            if (data.hasSkill(key)) {
                info = new BukkitSkillInfo(data.getSkill(key));
            } else info = SkillInfo.Empty;
            sender.send(SPackage.BUILDER.buildUpdateSkill(info));
        }
    }

    public void onUpdateCoolDownInfo(){
        if (status.isEnabled()) {
            PlayerAccounts accounts = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid));
            PlayerData data = accounts.getActiveData();
            Map<String, Integer> map = new HashMap<>();
            Map<Integer, String> nMap = this.accounts.getAccount(accounts.getActiveId()).getBarMap();

            nMap.values().forEach(key -> {
                if (data.hasSkill(key)) map.put(key, data.getSkill(key).getCooldown());
            });

            sender.send(SPackage.BUILDER.buildCoolDown(map));
        }
    }

    public void onLeaveCondition(){
        onLeaveCondition(false);
    }

    public void onLeaveCondition(boolean isListBar){
        ClientAccount account = accounts.getAccount(getActiveId());
        account.setCondition(null);
        sender.send(SPackage.BUILDER.buildLeaveCondition());
        if (isListBar) {
            sender.send(SPackage.BUILDER.buildListBar(account.getBarMap()));
        }
    }

    public void onMatchCondition(Condition condition){
        onMatchCondition(condition, false);
    }

    public void onMatchCondition(Condition condition, boolean listBar){
        ClientAccount account = accounts.getAccount(getActiveId());
        account.setCondition(condition);
        sender.send(SPackage.BUILDER.buildEnterCondition(condition));
        if (listBar) sender.send(SPackage.BUILDER.buildListBar(account.getBarMap()));
    }

    public void onPluginDisable(){
        sender.send(SPackage.BUILDER.buildLeaveCondition());
        sender.send(SPackage.BUILDER.buildDisable());
    }

    public void sendAccount() {
        if (status.isDiscovered() && !status.isBlocked()) {
            int active = getActiveId();
            if (status.getClientAccount() != active) {
                status.setClientAccount(active);
                sender.send(SPackage.BUILDER.buildAccount(active));
            }
            if (!status.isCached(active)) {
                List<SkillInfo> list = new ArrayList<>();
                for (PlayerSkill skill : SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid)).getSkills()) {
                    list.add(new BukkitSkillInfo(skill));
                }
                status.cache(active);
                sender.send(SPackage.BUILDER.buildListSkill(list));
            }
            sender.send(SPackage.BUILDER.buildListBar(accounts.getAccount(active).getBarMap()));
        }
    }

    public void sendEnable(){
        if (status.isDiscovered() && !status.isEnabled()) {
            status.enable();
            sender.send(SPackage.BUILDER.buildEnable());
        }
    }
    public void sendDisable(){
        if (status.isDiscovered() && status.isEnabled()) {
            status.disable();
            sender.send(SPackage.BUILDER.buildDisable());
        }
    }

    private int getActiveId(){
        return SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
    }
}
