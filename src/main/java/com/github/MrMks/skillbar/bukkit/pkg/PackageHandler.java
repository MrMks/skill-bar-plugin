package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.bukkit.Setting;
import com.github.MrMks.skillbar.bukkit.condition.Condition;
import com.github.MrMks.skillbar.bukkit.condition.ConditionData;
import com.github.MrMks.skillbar.bukkit.data.ClientBar;
import com.github.MrMks.skillbar.bukkit.data.ClientStatus;
import com.github.MrMks.skillbar.bukkit.manager.ConditionManager;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.handler.IServerHandler;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class PackageHandler implements IServerHandler {
    private final UUID uuid;
    private final ClientStatus status;
    private final ClientBar bar;
    private final PluginSender sender;
    private final ConditionData conditionData;
    public PackageHandler(UUID uuid, ClientStatus status, ClientBar bar, ConditionData conditionData, PluginSender sender){
        this.uuid = uuid;
        this.status = status;
        this.bar = bar;
        this.sender = sender;
        this.conditionData = conditionData;
    }

    private boolean checkValid(){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        PlayerData playerData = SkillAPI.getPlayerData(player);
        return player != null
                && SkillAPI.isLoaded()
                && player.isOnline()
                && SkillAPI.getSettings().isWorldEnabled(player.getPlayer().getWorld())
                && SkillAPI.hasPlayerData(player)
                && playerData.getClasses().size() > 0
                && playerData.getSkills().size() > 0;
    }

    private List<String> getProfessionKeys(PlayerData data) {
        List<String> list = new ArrayList<>();
        data.getClasses().forEach(pro->list.add(pro.getData().getName()));
        return list;
    }

    private List<SkillInfo> getSkillInfoList(PlayerData data) {
        List<SkillInfo> list = new ArrayList<>();
        data.getSkills().forEach(skill -> list.add(new BukkitSkillInfo(skill)));
        return list;
    }

    @Override
    public void onDiscover() {
        if (!status.isDiscovered()) {
            status.discover();
            sender.send(SPackage.BUILDER.buildSetting(Setting.getInstance().getBarMaxLine()));
            if (!status.isBlocked()) {
                if (checkValid()) {
                    Player player = Bukkit.getPlayer(uuid);
                    PlayerAccounts accounts = SkillAPI.getPlayerAccountData(player);
                    int active = accounts.getActiveId();

                    if (status.getClientAccount() != active) {
                        status.setClientAccount(active);
                        sender.send(SPackage.BUILDER.buildAccount(active));
                    }

                    if (!status.isCached(active)) {
                        status.cache(active);
                        sender.send(SPackage.BUILDER.buildListSkill(getSkillInfoList(accounts.getActiveData())));
                    }

                    Optional<Condition> optional = ConditionManager.match(player.getWorld(), getProfessionKeys(accounts.getActiveData()));
                    if (optional.isPresent()){
                        Condition condition = optional.get();
                        conditionData.setCondition(condition);
                        sender.send(SPackage.BUILDER.buildEnterCondition(condition));
                        if (!condition.isEnableFix() || condition.isEnableFree()) sender.send(SPackage.BUILDER.buildListBar(conditionData.getConditionBar()));
                    } else {
                        sender.send(SPackage.BUILDER.buildListBar(bar.getAccountBar()));
                    }
                    status.enable();
                    sender.send(SPackage.BUILDER.buildEnable());
                }
            }
        }
    }

    @Override
    public void onListSkill(List<String> keys) {
        if (status.isEnabled() && checkValid()){
            PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
            List<String> reList = new ArrayList<>();
            for (String key : keys) if (!data.hasSkill(key)) reList.add(key);
            List<SkillInfo> aList = new ArrayList<>();
            for (PlayerSkill skill : data.getSkills()){
                if (!keys.contains(skill.getData().getKey())) {
                    aList.add(new BukkitSkillInfo(skill));
                }
            }
            sender.send(SPackage.BUILDER.buildAddSkill(aList));
            if (!reList.isEmpty()) sender.send(SPackage.BUILDER.buildRemoveSkill(reList));
        }
    }

    @Override
    public void onUpdateSkill(String key) {
        if (checkValid()){
            PlayerData data = SkillAPI.getPlayerData(Bukkit.getPlayer(uuid));
            SkillInfo info;
            if (data.hasSkill(key)) {
                PlayerSkill skill = data.getSkill(key);
                info = new BukkitSkillInfo(skill);
                sender.send(SPackage.BUILDER.buildUpdateSkill(info));
            } else {
                sender.send(SPackage.BUILDER.buildRemoveSkill(Collections.singletonList(key)));
            }
        }
    }

    @Override
    public void onListBar() {
        if (checkValid()){
            Map<Integer, String> map = new HashMap<>();
            Optional<Condition> optional = conditionData.getCondition();
            Player player = Bukkit.getPlayer(uuid);
            PlayerData playerData = SkillAPI.getPlayerData(player);
            if (optional.isPresent()) {
                map.putAll(conditionData.getConditionBar());
                int size = map.size();
                map.values().removeIf(v->!playerData.hasSkill(v));
                if (size != map.size()) conditionData.setBar(map);
            } else {
                map.putAll(bar.getAccountBar());
                int size = map.size();
                map.values().removeIf(v->!playerData.hasSkill(v));
                if (map.size() != size) bar.setAccountBar(map);
            }
            sender.send(SPackage.BUILDER.buildListBar(map));
        }
    }

    @Override
    public void onSaveBar(Map<Integer, String> map) {
        if (checkValid()){
            int size = map.size();
            Optional<Condition> optional = conditionData.getCondition();
            Player player = Bukkit.getPlayer(uuid);
            PlayerData playerData = SkillAPI.getPlayerData(player);
            map.values().removeIf(v->!playerData.hasSkill(v));
            boolean flag = size != map.size();
            if (optional.isPresent()) {
                Condition condition = optional.get();
                Map<Integer, String> fM = condition.getFixMap();
                List<Integer> fL = condition.getFreeList();
                size = map.size();
                if (condition.isEnableFree() || !condition.isEnableFix()) {
                    map.keySet().removeIf(v -> fM.containsKey(v) && !fL.contains(v) && !fL.contains(-1));
                    fM.forEach(map::putIfAbsent);
                    flag = flag || size != map.size();
                    conditionData.setBar(map);
                } else {
                    map.keySet().removeIf(v->!fM.containsKey(v));
                    flag = size != map.size();
                    size = map.size();
                    fM.forEach(map::put);
                    flag = flag || size != map.size();
                }
            } else {
                bar.setAccountBar(map);
            }
            if (flag) sender.send(SPackage.BUILDER.buildListBar(map));
        }
    }

    @Override
    public void onCast(String key) {
        if (checkValid()){
            Player player = Bukkit.getPlayer(uuid);
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
            sender.send(SPackage.BUILDER.buildCast(key, exist, suc));
            if (exist && suc) {
                Map<String, Integer> map = new HashMap<>(1);
                map.put(key, playerData.getSkill(key).getCooldown());
                sender.send(SPackage.BUILDER.buildCoolDown(map));
            }
        }
    }
}
