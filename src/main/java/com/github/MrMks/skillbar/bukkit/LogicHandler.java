package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.condition.Condition;
import com.github.MrMks.skillbar.bukkit.data.ClientAccount;
import com.github.MrMks.skillbar.bukkit.data.ClientAccounts;
import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.bukkit.data.ClientStatus;
import com.github.MrMks.skillbar.bukkit.manager.ConditionManager;
import com.github.MrMks.skillbar.bukkit.pkg.BukkitSkillInfo;
import com.github.MrMks.skillbar.bukkit.pkg.MessageSender;
import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class LogicHandler {
    private final MessageSender sender;
    public LogicHandler(MessageSender sender){
        this.sender = sender;
    }

    /**
     * player join, start discover
     * send if not discovered
     */
    public void onJoin(ClientData data){
        if (checkNull(data)) {
            if (!data.getStatus().isDiscovered()) {
                send(data, SPackage.BUILDER.buildDiscover());
            }
        }
    }

    /**
     * player reset profess
     * send if discovered, not blocked, and enabled
     * will set status to disabled
     */
    public void onResetProfess(ClientData data){
        if (checkNull(data)) {
            ClientStatus status = data.getStatus();
            if (status.isEnabled()) {
                int active = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(data.getUniqueId())).getActiveId();
                status.cleanCache(active);
                send(data, SPackage.BUILDER.buildCleanUp(active));
                sendDisable(data);
                data.getAccounts().getAccount(active).clean();
            }
        }
    }

    /**
     * player profess a class from null
     * send if discovered, not blocked and not enabled 
     */
    public void onStartProfess(ClientData data){
        if (checkNull(data)) {
            ClientStatus status = data.getStatus();
            if (status.isDiscovered() && !status.isBlocked() && !status.isEnabled()) {
                sendAccount(data);
                sendEnable(data);
            }
        }
    }

    public void onChangeProfess(ClientData cData, PlayerClass playerClass){
        if (checkNull(cData)) {
            ClientStatus status = cData.getStatus();
            if (status.isEnabled()) {
                List<Skill> skills = playerClass.getData().getSkills();
                List<SkillInfo> infoList = new ArrayList<>();
                PlayerData data = playerClass.getPlayerData();
                for (Skill skill : skills) {
                    String skillKey = skill.getKey();
                    if (data.hasSkill(skillKey)) {
                        BukkitSkillInfo info = new BukkitSkillInfo(data.getSkill(skillKey));
                        if (info.getItemId() != 0) infoList.add(info);
                    }
                }
                send(cData, SPackage.BUILDER.buildAddSkill(infoList));
            }
        }
    }

    public void onAccountSwitch(ClientData data){
        if (checkNull(data)) {
            if (data.getStatus().isEnabled()) {
                sendAccount(data);
            }
        }
    }

    public void onAccToEnable(ClientData data){
        if (checkNull(data)) {
            sendAccount(data);
            sendEnable(data);
        }
    }

    public void onAccToDisable(ClientData data){
        if (checkNull(data)) {
            sendDisable(data);
            sendAccount(data);
        }
    }

    public void onWorldToEnable(ClientData data){
        if (checkNull(data)) {
            sendAccount(data);
            sendEnable(data);
        }
    }
    public void onWorldToDisable(ClientData data){
        if (checkNull(data)) {
            sendDisable(data);
        }
    }

    public void onUpdateSkillInfo(ClientData cData, String key){
        if (checkNull(cData)) {
            ClientStatus status = cData.getStatus();
            UUID uuid = cData.getUniqueId();
            if (status.isDiscovered() && !status.isBlocked()) {
                PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
                SkillInfo info;
                if (data.hasSkill(key)) {
                    info = new BukkitSkillInfo(data.getSkill(key));
                    if (info.getItemId() == 0) info = SkillInfo.Empty;
                } else info = SkillInfo.Empty;
                send(cData, SPackage.BUILDER.buildUpdateSkill(info));
            }
        }
    }

    public void onUpdateCoolDownInfo(ClientData cData){
        if (checkValid(cData)) {
            ClientStatus status = cData.getStatus();
            if (status.isEnabled()) {
                PlayerAccounts accounts = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(cData.getUniqueId()));
                PlayerData data = accounts.getActiveData();
                Map<String, Integer> map = new HashMap<>();
                Map<Integer, String> nMap = cData.getAccounts().getAccount(accounts.getActiveId()).getBarMap();

                nMap.values().forEach(key -> {
                    if (data.hasSkill(key)) map.put(key, data.getSkill(key).getCooldown());
                });

                send(cData, SPackage.BUILDER.buildCoolDown(map));
            }
        }
    }

    public void onLeaveCondition(ClientData data){
        onLeaveCondition(data, false);
    }

    public void onLeaveCondition(ClientData data, boolean isListBar){
        if (checkNull(data)) {
            ClientAccount account = data.getAccounts().getAccount(getActiveId(data));
            account.setCondition(null);
            send(data, SPackage.BUILDER.buildLeaveCondition());
            if (isListBar) {
                send(data, SPackage.BUILDER.buildListBar(account.getBarMap()));
            }
        }
    }

    public void onMatchCondition(ClientData data, Condition condition){
        onMatchCondition(data, condition, false);
    }

    public void onMatchCondition(ClientData data, Condition condition, boolean listBar){
        if (checkNull(data)) {
            ClientAccount account = data.getAccounts().getAccount(getActiveId(data));
            account.setCondition(condition);
            send(data, SPackage.BUILDER.buildEnterCondition(condition));
            if (listBar) send(data, SPackage.BUILDER.buildListBar(account.getBarMap()));
        }
    }

    public void onPluginDisable(ClientData data){
        if (checkNull(data)) {
            send(data, SPackage.BUILDER.buildLeaveCondition());
            send(data, SPackage.BUILDER.buildDisable());
        }
    }

    public void onDiscover(ClientData data){
        if (checkNull(data)) {
            ClientStatus status = data.getStatus();
            UUID uuid = data.getUniqueId();
            if (!status.isDiscovered()) {
                status.discover();
                send(data, SPackage.BUILDER.buildSetting(Setting.getInstance().getBarMaxLine()));
                if (!status.isBlocked()) {
                    if (data.isValid()) {
                        Player player = Bukkit.getPlayer(uuid);
                        PlayerAccounts accounts = SkillAPI.getPlayerAccountData(player);
                        int active = accounts.getActiveId();

                        if (status.getClientAccount() != active) {
                            status.setClientAccount(active);
                            send(data, SPackage.BUILDER.buildAccount(active));
                        }

                        if (!status.isCached(active)) {
                            status.cache(active);
                            send(data, SPackage.BUILDER.buildListSkill(getSkillInfoList(accounts.getActiveData())));
                        }

                        Optional<Condition> optional = ConditionManager.match(player.getWorld(), getProfessionKeys(accounts.getActiveData()));
                        if (optional.isPresent()) {
                            Condition condition = optional.get();
                            data.getAccounts().getAccount(active).setCondition(condition);
                            send(data, SPackage.BUILDER.buildEnterCondition(condition));
                        }
                        send(data, SPackage.BUILDER.buildListBar(data.getAccounts().getAccount(active).getBarMap()));

                        status.enable();
                        send(data, SPackage.BUILDER.buildEnable());
                    }
                }
            }
        }
    }

    private List<String> getProfessionKeys(PlayerData data) {
        List<String> list = new ArrayList<>();
        data.getClasses().forEach(pro->list.add(pro.getData().getName()));
        return list;
    }

    private List<SkillInfo> getSkillInfoList(PlayerData data) {
        List<SkillInfo> list = new ArrayList<>();
        data.getSkills().forEach(skill -> {
            SkillInfo info = new BukkitSkillInfo(skill);
            if (info.getItemId() != 0) list.add(info);
        });
        return list;
    }

    public void onListSkill(ClientData data, List<String> keys){
        if (checkValid(data)) {
            UUID uuid = data.getUniqueId();
            PlayerData playerData = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
            List<String> reList = new ArrayList<>();
            for (String key : keys) if (!playerData.hasSkill(key)) reList.add(key);
            List<SkillInfo> aList = new ArrayList<>();
            for (PlayerSkill skill : playerData.getSkills()) {
                if (!keys.contains(skill.getData().getKey())) {
                    SkillInfo info = new BukkitSkillInfo(skill);
                    if (info.getItemId() != 0) aList.add(info);
                }
            }
            send(data, SPackage.BUILDER.buildAddSkill(aList));
            if (!reList.isEmpty()) send(data, SPackage.BUILDER.buildRemoveSkill(reList));
        }
    }

    public void onUpdateSkill(ClientData data, String key){
        if (checkValid(data)) {
            UUID uuid = data.getUniqueId();
            PlayerData pData = SkillAPI.getPlayerData(Bukkit.getPlayer(uuid));
            SkillInfo info;
            if (pData.hasSkill(key)) {
                PlayerSkill skill = pData.getSkill(key);
                info = new BukkitSkillInfo(skill);
                send(data, SPackage.BUILDER.buildUpdateSkill(info));
            } else {
                send(data, SPackage.BUILDER.buildRemoveSkill(Collections.singletonList(key)));
            }
        }
    }

    public void onListBar(ClientData data){
        if (checkValid(data)) {
            ClientAccounts accounts = data.getAccounts();
            UUID uuid = data.getUniqueId();
            Map<Integer, String> map = new HashMap<>(accounts.getAccount(getActiveId(data)).getBarMap());
            int size = map.size();
            Player player = Bukkit.getPlayer(uuid);
            PlayerData playerData = SkillAPI.getPlayerData(player);
            map.values().removeIf(v -> !playerData.hasSkill(v));
            if (size != map.size()) accounts.getAccount(getActiveId(data)).setBarMap(map);
            send(data, SPackage.BUILDER.buildListBar(map));
        }
    }

    public void onSaveBar(ClientData data, Map<Integer, String> map){
        if (checkValid(data)) {
            int size = map.size();
            Player player = Bukkit.getPlayer(data.getUniqueId());
            PlayerData playerData = SkillAPI.getPlayerData(player);
            map.values().removeIf(v -> !playerData.hasSkill(v));
            boolean flag = size != map.size();
            flag = flag || data.getAccounts().getAccount(getActiveId(data)).setBarMap(map);
            if (flag) send(data, SPackage.BUILDER.buildListBar(map));
        }
    }

    public void onCast(ClientData data, String key){
        if (checkValid(data)) {
            Player player = Bukkit.getPlayer(data.getUniqueId());
            PlayerData playerData = SkillAPI.getPlayerData(player);
            boolean exist, suc;
            if (playerData.hasSkill(key)) {
                exist = true;
                if (!playerData.getSkill(key).isUnlocked()) {
                    suc = false;
                } else {
                    suc = playerData.cast(key);
                }
            } else {
                exist = false;
                suc = false;
            }
            send(data, SPackage.BUILDER.buildCast(key, exist, suc));
            if (exist && suc) {
                Map<String, Integer> map = new HashMap<>(1);
                map.put(key, playerData.getSkill(key).getCooldown());
                send(data, SPackage.BUILDER.buildCoolDown(map));
            }
        }
    }

    public void sendAccount(ClientData data) {
        if (checkNull(data)) {
            ClientStatus status = data.getStatus();
            if (status.isDiscovered() && !status.isBlocked()) {
                int active = getActiveId(data);
                if (status.getClientAccount() != active) {
                    status.setClientAccount(active);
                    send(data, SPackage.BUILDER.buildAccount(active));
                }
                if (!status.isCached(active)) {
                    List<SkillInfo> list = new ArrayList<>();
                    for (PlayerSkill skill : SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(data.getUniqueId())).getSkills()) {
                        SkillInfo info = new BukkitSkillInfo(skill);
                        if (info.getItemId() != 0) list.add(info);
                    }
                    status.cache(active);
                    send(data, SPackage.BUILDER.buildListSkill(list));
                }
                send(data, SPackage.BUILDER.buildListBar(data.getAccounts().getAccount(active).getBarMap()));
            }
        }
    }

    public void sendEnable(ClientData data){
        if (checkNull(data)) {
            ClientStatus status = data.getStatus();
            if (status.isDiscovered() && !status.isEnabled()) {
                status.enable();
                send(data, SPackage.BUILDER.buildEnable());
            }
        }
    }

    public void sendDisable(ClientData data){
        if (checkNull(data)) {
            ClientStatus status = data.getStatus();
            if (status.isDiscovered() && status.isEnabled()) {
                status.disable();
                send(data, SPackage.BUILDER.buildDisable());
            }
        }
    }

    private int getActiveId(ClientData data){
        return SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(data.getUniqueId())).getActiveId();
    }

    private void send(ClientData data, ByteBuilder builder) {
        sender.send(data.getUniqueId(), data.getStatus().getPackageIndex(), builder);
    }

    private boolean checkValid(ClientData data) {
        return checkNull(data) && data.isValid();
    }

    private boolean checkNull(ClientData data) {
        return data != null;
    }
}
