package com.github.MrMks.skillbar.pkg;

import com.github.MrMks.skillbar.Setting;
import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.handler.IServerHandler;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.github.MrMks.skillbar.data.ClientBar;
import com.github.MrMks.skillbar.data.ClientStatus;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class PackageHandler implements IServerHandler {
    private UUID uuid;
    private ClientStatus status;
    private ClientBar bar;
    private PluginSender sender;
    public PackageHandler(UUID uuid,  ClientStatus status, ClientBar bar, PluginSender sender){
        this.uuid = uuid;
        this.status = status;
        this.bar = bar;
        this.sender = sender;
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

    @Override
    public void onDiscover() {
        if (!status.isDiscovered()) {
            status.discover();
            if (!status.isBlocked()) {
                sender.send(SPackage.BUILDER.buildSetting(BukkitByteBuilder::new, Setting.getInstance().getBarMaxLine()));
                if (checkValid()) {
                    PlayerAccounts accounts = SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid));
                    status.enable();
                    sender.send(SPackage.BUILDER.buildEnable(BukkitByteBuilder::new, accounts.getActiveId(), accounts.getActiveData().getSkills().size()));
                }
            }
        }
    }

    @Override
    public void onListSkill(List<CharSequence> keys) {
        if (status.isEnable() && checkValid()){
            ArrayList<String> reList = new ArrayList<>();
            PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
            for (CharSequence key : keys){
                if (!data.hasSkill(key.toString())) reList.add(key.toString());
            }
            ArrayList<SkillInfo> aList = new ArrayList<>();
            for (PlayerSkill skill : data.getSkills()){
                if (!keys.contains(skill.getData().getKey())) {
                    aList.add(new BukkitSkillInfo(skill));
                }
            }
            ByteBuilder builder = SPackage.BUILDER.buildListSkill(BukkitByteBuilder::new,aList,reList);
            sender.send(builder);
        }
    }

    @Override
    public void onUpdateSkill(CharSequence key) {
        if (checkValid()){
            PlayerData data = SkillAPI.getPlayerData(Bukkit.getPlayer(uuid));
            SkillInfo info;
            if (data.hasSkill(key.toString())) {
                PlayerSkill skill = data.getSkill(key.toString());
                info = new BukkitSkillInfo(skill);
            } else {
                info = new BukkitSkillInfo(key.toString());
            }
            sender.send(SPackage.BUILDER.buildUpdateSkill(BukkitByteBuilder::new,info));
        }
    }

    @Override
    public void onListBar() {
        if (checkValid()){
            Player player = Bukkit.getPlayer(uuid);
            PlayerData playerData = SkillAPI.getPlayerData(player);
            Map<Integer, String> map = new HashMap<>();
            for (int index : bar.keys()) {
                if (playerData.hasSkill(bar.getSkill(index))) map.put(index,bar.getSkill(index));
            }
            bar.setBar(SkillAPI.getPlayerAccountData(player).getActiveId(),map);
            sender.send(SPackage.BUILDER.buildListBar(BukkitByteBuilder::new,map));
        }
    }

    @Override
    public void onSaveBar(Map<Integer, CharSequence> map) {
        if (checkValid()){
            Player player = Bukkit.getPlayer(uuid);
            PlayerData playerData = SkillAPI.getPlayerData(player);
            PlayerAccounts accounts = SkillAPI.getPlayerAccountData(player);
            Map<Integer, String> nMap = new HashMap<>();
            for (Map.Entry<Integer, CharSequence> entry : map.entrySet()) {
                if (playerData.hasSkill(entry.getValue().toString())) {
                    nMap.put(entry.getKey(), entry.getValue().toString());
                }
            }
            bar.setBar(accounts.getActiveId(), nMap);
            if (map.size() != nMap.size()) {
                sender.send(SPackage.BUILDER.buildListBar(BukkitByteBuilder::new, nMap));
            }
        }
    }

    @Override
    public void onCast(CharSequence key) {
        if (checkValid()){
            Player player = Bukkit.getPlayer(uuid);
            PlayerData playerData = SkillAPI.getPlayerData(player);
            boolean exist, suc;
            byte code;
            if (playerData.hasSkill(key.toString())) {
                exist = true;
                if (!playerData.getSkill(key.toString()).isUnlocked()) {
                    suc = false;
                    code = Constants.CAST_FAILED_UNLOCK;
                } else {
                    suc = playerData.cast(key.toString());
                    code = suc ? Constants.CAST_SUCCESS : Constants.CAST_FAILED_UNEXPECTED;
                }
            } else {
                exist = false;
                suc = false;
                code = Constants.CAST_FAILED_NO_SKILL;
            }
            sender.send(SPackage.BUILDER.buildCast(BukkitByteBuilder::new, key.toString(), exist, suc, code));
        }
    }
}
