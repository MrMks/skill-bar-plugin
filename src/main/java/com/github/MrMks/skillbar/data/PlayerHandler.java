package com.github.MrMks.skillbar.data;

import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.SkillInfo;
import com.github.MrMks.skillbar.common.handler.IServerHandler;
import com.github.MrMks.skillbar.common.pkg.SPackage;
import com.github.MrMks.skillbar.pkg.BukkitByteAllocator;
import com.github.MrMks.skillbar.pkg.BukkitSkillInfo;
import com.github.MrMks.skillbar.pkg.PluginSender;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class PlayerHandler implements IServerHandler {
    private static int itemMethodFlag = 0;
    private static ItemStack getItemStack(PlayerSkill skill){
        if (itemMethodFlag == 0) {
            try {
                Skill.class.getMethod("getIndicator", PlayerSkill.class, boolean.class);
                itemMethodFlag += 1;
            }catch (NoSuchMethodException ignored){}
            try {
                //noinspection JavaReflectionMemberAccess
                Skill.class.getMethod("getIndicator", PlayerSkill.class);
                itemMethodFlag += 2;
            }catch (NoSuchMethodException ignored){}
        }
        ItemStack stack = null;
        switch (itemMethodFlag){
            case 1:
                try {
                    Method method = Skill.class.getMethod("getIndicator", PlayerSkill.class, boolean.class);
                    stack = (ItemStack) method.invoke(skill.getData(),skill, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
                break;
            case 2:
                try {
                    // method used in non-premium skillapi
                    @SuppressWarnings("JavaReflectionMemberAccess")
                    Method method = Skill.class.getMethod("getIndicator", PlayerSkill.class);
                    stack = (ItemStack) method.invoke(skill.getData(), skill);
                } catch (Exception ignored){}
                break;
            default:
                stack = new ItemStack(Material.AIR);
                break;
        }
        return stack;
    }

    private ClientData data;
    private PlayerBar bar;
    private PluginSender sender;
    public PlayerHandler(UUID uuid){
        this.data = new ClientData(uuid);
        this.bar = new PlayerBar(uuid);
        this.sender = new PluginSender(uuid);
    }

    private void receive(){
        data.onReceive();
    }

    private boolean checkValid(){
        OfflinePlayer player = Bukkit.getOfflinePlayer(data.getUid());
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
        receive();
        if (!data.isDiscovered() && !data.isBlocked()) data.discover();
    }

    @Override
    public void onListSkill(List<CharSequence> keys) {
        receive();
        if (checkValid()){
            ArrayList<String> reList = new ArrayList<>();
            PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(this.data.getUid()));
            for (CharSequence key : keys){
                if (!data.hasSkill(key.toString())) reList.add(key.toString());
            }
            ArrayList<SkillInfo> aList = new ArrayList<>();
            for (PlayerSkill skill : data.getSkills()){
                if (!keys.contains(skill.getData().getKey())) {
                    aList.add(new BukkitSkillInfo(skill.getData().getKey(), skill.isUnlocked(), skill.getData().canCast(),getItemStack(skill)));
                }
            }
            ByteBuilder builder = SPackage.BUILDER.buildListSkill(BukkitByteAllocator.DEFAULT,aList,reList);
            sender.send(builder);
        }
    }

    @Override
    public void onUpdateSkill(CharSequence key) {
        receive();
        if (checkValid()){
            PlayerData data = SkillAPI.getPlayerData(Bukkit.getPlayer(this.data.getUid()));
            SkillInfo info;
            if (data.hasSkill(key.toString())) {
                PlayerSkill skill = data.getSkill(key.toString());
                info = new BukkitSkillInfo(skill.getData().getKey(),skill.isUnlocked(),skill.getData().canCast(),getItemStack(skill));
            } else {
                info = new SkillInfo("",false,false,0,(short) 0,"",new ArrayList<>());
            }
            sender.send(SPackage.BUILDER.buildUpdateSkill(BukkitByteAllocator.DEFAULT,info));
        }
    }

    @Override
    public void onListBar() {
        receive();
        if (checkValid()){
            PlayerBar bar = data.getBar();
            Player player = Bukkit.getPlayer(data.getUid());
            PlayerData playerData = SkillAPI.getPlayerData(player);
            Map<Integer, String> map = new HashMap<>();
            for (int index : bar.keys()) {
                if (playerData.hasSkill(bar.getSkill(index))) map.put(index,bar.getSkill(index));
            }
            bar.setBar(SkillAPI.getPlayerAccountData(player).getActiveId(),map);
            sender.send(SPackage.BUILDER.buildListBar(BukkitByteAllocator.DEFAULT,map));
        }
    }

    @Override
    public void onSaveBar(Map<Integer, CharSequence> map) {
        receive();
        if (checkValid()){
            PlayerBar bar = data.getBar();
            Player player = Bukkit.getPlayer(data.getUid());
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
                sender.send(SPackage.BUILDER.buildListBar(BukkitByteAllocator.DEFAULT, nMap));
            }
        }
    }

    @Override
    public void onCast(CharSequence key) {
        receive();
        if (checkValid()){
            Player player = Bukkit.getPlayer(data.getUid());
            PlayerData playerData = SkillAPI.getPlayerData(player);
            boolean exist, suc;
            byte code;
            if (playerData.hasSkill(key.toString())) {
                exist = true;
                if (playerData.getSkill(key.toString()).isUnlocked()) {
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
            sender.send(SPackage.BUILDER.buildCast(BukkitByteAllocator.DEFAULT, key.toString(), exist, suc, code));
        }
    }

    public ClientData getData() {
        return data;
    }
}
