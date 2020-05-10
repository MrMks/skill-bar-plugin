package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.bukkit.Setting;
import com.github.MrMks.skillbar.bukkit.condition.Condition;
import com.github.MrMks.skillbar.bukkit.data.ClientAccounts;
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
    private final PluginSender sender;
    private final ClientAccounts accounts;
    public PackageHandler(UUID uuid, ClientStatus status, ClientAccounts accounts, PluginSender sender){
        this.uuid = uuid;
        this.status = status;
        this.sender = sender;
        this.accounts = accounts;
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

    private int getActiveId(){
        return SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
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
                        this.accounts.getAccount(active).setCondition(condition);
                        sender.send(SPackage.BUILDER.buildEnterCondition(condition));
                    }
                    sender.send(SPackage.BUILDER.buildListBar(this.accounts.getAccount(active).getBarMap()));

                    status.enable();
                    sender.send(SPackage.BUILDER.buildEnable());
                }
            }
        }
    }

    @Override
    public void onListSkill(List<String> keys) {
        if (checkValid()){
            PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
            List<String> reList = new ArrayList<>();
            for (String key : keys) if (!data.hasSkill(key)) reList.add(key);
            List<SkillInfo> aList = new ArrayList<>();
            for (PlayerSkill skill : data.getSkills()){
                if (!keys.contains(skill.getData().getKey())) {
                    SkillInfo info = new BukkitSkillInfo(skill);
                    if (info.getItemId() != 0) aList.add(info);
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
            Map<Integer, String> map = new HashMap<>(accounts.getAccount(getActiveId()).getBarMap());
            int size = map.size();
            Player player = Bukkit.getPlayer(uuid);
            PlayerData playerData = SkillAPI.getPlayerData(player);
            map.values().removeIf(v->!playerData.hasSkill(v));
            if (size != map.size()) accounts.getAccount(getActiveId()).setBarMap(map);
            sender.send(SPackage.BUILDER.buildListBar(map));
        }
    }

    @Override
    public void onSaveBar(Map<Integer, String> map) {
        if (checkValid()){
            int size = map.size();
            Player player = Bukkit.getPlayer(uuid);
            PlayerData playerData = SkillAPI.getPlayerData(player);
            map.values().removeIf(v->!playerData.hasSkill(v));
            boolean flag = size != map.size();
            flag = flag || accounts.getAccount(getActiveId()).setBarMap(map);
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
