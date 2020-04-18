package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.condition.Condition;
import com.github.MrMks.skillbar.bukkit.data.ClientBar;
import com.github.MrMks.skillbar.bukkit.data.ClientStatus;
import com.github.MrMks.skillbar.bukkit.pkg.BukkitByteBuilder;
import com.github.MrMks.skillbar.bukkit.pkg.BukkitSkillInfo;
import com.github.MrMks.skillbar.bukkit.pkg.PluginSender;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.Bukkit;

import java.util.*;

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

    /**
     * player join, start discover
     * send if not discovered
     */
    public void onJoin(){
        if (!status.isDiscovered()) {
            sender.send(SPackage.BUILDER.buildDiscover(BukkitByteBuilder::new, Constants.VERSION));
        }
    }

    /**
     * player reset profess
     * send if discovered, not blocked, and enabled
     * will set status to disabled
     */
    public void onResetProfess(){
        if (status.isDiscovered() && status.isEnable()) {
            int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
            sender.send(SPackage.BUILDER.buildCleanUp(BukkitByteBuilder::new, active));
            sendDisable();
            bar.setBar(active, Collections.emptyMap());
        }
    }

    /**
     * player profess a class from null
     * send if discovered, not blocked and not enabled 
     */
    public void onStartProfess(){
        if (status.isDiscovered() && !status.isBlocked() && !status.isEnable()) {
            sendAccount();
            sendEnable();
        }
    }

    public void onChangeProfess(PlayerClass playerClass){
        if (status.isDiscovered() && status.isEnable()) {
            List<Skill> skills = playerClass.getData().getSkills();
            List<SkillInfo> infoList = new ArrayList<>();
            PlayerData data = playerClass.getPlayerData();
            for (Skill skill : skills) {
                String skillKey = skill.getKey();
                if (data.hasSkill(skillKey)) infoList.add(new BukkitSkillInfo(data.getSkill(skillKey)));
            }
            sender.send(SPackage.BUILDER.buildAddSkill(BukkitByteBuilder::new, infoList));
        }
    }

    public void onAccountSwitch(){
        if (status.isDiscovered() && status.isEnable()) {
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
        sendEnable();
    }
    public void onWorldToDisable(){
        sendDisable();
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
        Optional<Condition> optional = status.getCondition();
        boolean flag = optional.isPresent() && optional.get().isEnableFix() && optional.get().isAllowFreeSlots();
        PlayerAccounts accounts = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid));
        PlayerData data = accounts.getActiveData();
        Map<String, Integer> map = new HashMap<>();
        if (flag) {
            optional.get().getBarList().values().forEach(key->{
                if (data.hasSkill(key)) map.put(key, data.getSkill(key).getCooldown());
            });
            bar.getConditionMap().values().forEach(key->{
                if (data.hasSkill(key)) map.put(key, data.getSkill(key).getCooldown());
            });
        }
        else bar.keys().forEach(index->{
            String key = bar.getSkill(index);
            if (data.hasSkill(key)) map.put(key, data.getSkill(key).getCooldown());
        });
        sender.send(SPackage.BUILDER.buildCoolDown(BukkitByteBuilder::new, map));
    }

    public void onLeaveCondition(){
        onLeaveCondition(false);
    }

    public void onLeaveCondition(boolean isListBar){
        if (status.getCondition().isPresent()) {
            status.leaveCondition();
            sender.send(SPackage.BUILDER.buildFixBar(BukkitByteBuilder::new, false));
            sender.send(SPackage.BUILDER.buildSetting(BukkitByteBuilder::new, Setting.getInstance().getBarMaxLine()));
            Map<Integer, String> map = new HashMap<>();
            if (isListBar) bar.keys().forEach(index->map.put(index,bar.getSkill(index)));
            sender.send(SPackage.BUILDER.buildListBar(BukkitByteBuilder::new,map));
        }
    }

    public void onMatchCondition(Condition condition){
        onMatchCondition(condition, false);
    }

    public void onMatchCondition(Condition condition, boolean listBar){
        Optional<Condition> optional = status.getCondition();
        if (!optional.isPresent() || !optional.get().getKey().equals(condition.getKey())) {
            status.setCondition(condition);
            sender.send(SPackage.BUILDER.buildSetting(BukkitByteBuilder::new, condition.getBarSize() >= 0 ? condition.getBarSize() : Setting.getInstance().getBarMaxLine()));
            sender.send(SPackage.BUILDER.buildFixBar(BukkitByteBuilder::new, condition.isEnableFix()));
            if (condition.isEnableFix() && condition.isAllowFreeSlots()) sender.send(SPackage.BUILDER.buildFreeSlots(BukkitByteBuilder::new, condition.getFreeList()));
            if (listBar) sender.send(SPackage.BUILDER.buildListBar(BukkitByteBuilder::new, condition.getBarList()));
        }
    }

    public void onPluginDisable(){
        sender.send(SPackage.BUILDER.buildListBar(BukkitByteBuilder::new, Collections.emptyMap()));
        sender.send(SPackage.BUILDER.buildFixBar(BukkitByteBuilder::new,false));
        sender.send(SPackage.BUILDER.buildSetting(BukkitByteBuilder::new,Setting.getInstance().getBarMaxLine()));
        sender.send(SPackage.BUILDER.buildDisable(BukkitByteBuilder::new));
    }

    public void sendAccount() {
        if (status.isDiscovered() && !status.isBlocked()) {
            int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
            int size = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid)).getSkills().size();
            sender.send(SPackage.BUILDER.buildAccount(BukkitByteBuilder::new, active, size));
        }
    }

    public void sendEnable(){
        if (status.isDiscovered() && !status.isEnable()) {
            status.enable();
            sender.send(SPackage.BUILDER.buildEnable(BukkitByteBuilder::new));
        }
    }
    public void sendDisable(){
        if (status.isDiscovered() && status.isEnable()) {
            status.disable();
            sender.send(SPackage.BUILDER.buildDisable(BukkitByteBuilder::new));
        }
    }
}
