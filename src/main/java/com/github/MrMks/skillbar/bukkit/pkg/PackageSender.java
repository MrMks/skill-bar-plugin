package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.bukkit.manager.*;
import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import com.rit.sucy.version.VersionManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class PackageSender {

    private Logger logger;
    private Plugin plugin;
    private Manager manager;
    private byte itemMethodFlag = 0;
    public PackageSender(Plugin plugin, Manager manager){
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.manager = manager;
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

    public boolean isLoad(){
        return !(itemMethodFlag == 0);
    }

    public void sendDiscover(Player player){
        if (!manager.get(player).isDiscovered()) sendMessage(player, new BukkitByteBuilder(Constants.DISCOVER));
    }

    public void sendEnable(Player player){
        if (checkValid(player)){
            ClientData m = manager.get(player);
            if (m.getStatus() != ClientStatus.Enabled) {
                ByteBuilder builder = new BukkitByteBuilder(Constants.ENABLE);
                builder.writeInt(SkillAPI.getPlayerAccountData(player).getActiveId());
                builder.writeInt(SkillAPI.getPlayerData(player).getSkills().size());
                sendMessage(player, builder);
                m.enable();
            }
        } else {
            if (checkClient(player)){
                sendDisable(player);
            }
        }
    }

    public void sendAllDiscover() {
        for (Player player : VersionManager.getOnlinePlayers()){
            sendDiscover(player);
        }
    }

    public void sendListSkill(Player player, List<CharSequence> req){
        int v = 0;
        if (checkValid(player)) v += 1;
        if (checkClient(player)) v += 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("\u00A7cRequest send listSkill to a disabled client");
                break;
            case 2:
                sendDisable(player);
                break;
            case 3:
                ByteBuilder builder = new BukkitByteBuilder(Constants.LIST_SKILL);
                Collection<PlayerSkill> fullList = SkillAPI.getPlayerData(player).getSkills();
                HashMap<String, PlayerSkill> fullMap = new HashMap<>();
                List<String> remove = new ArrayList<>();
                for (PlayerSkill skill : fullList){
                    fullMap.put(skill.getData().getKey(), skill);
                }
                for (CharSequence sequence : req){
                    if (fullMap.containsKey(sequence.toString())) fullMap.remove(sequence.toString());
                    else remove.add(sequence.toString());
                }
                builder.writeInt(fullMap.size());
                for (PlayerSkill skill : fullMap.values()){
                    buildSkill(builder,skill);
                }
                builder.writeCharSequenceList(remove);

                sendMessage(player,builder);
                break;
        }
    }

    public void sendRemoveList(Player player, List<? extends CharSequence> req){
        int v = 0;
        if (checkValid(player)) v += 1;
        if (checkClient(player)) v += 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("§cRequest send listSkill to a disabled client");
                break;
            case 2:
                sendDisable(player);
                break;
            case 3:
                ByteBuilder builder = new BukkitByteBuilder(Constants.LIST_SKILL);
                builder.writeInt(0); // add skill list
                builder.writeCharSequenceList(req);
                sendMessage(player,builder);
                break;
        }
    }

    public void sendEnforceListSkill(Player player){
        int v = 0;
        if (checkValid(player)) v += 1;
        if (checkClient(player)) v += 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("§cRequest send enforceListSkill to a disabled client");
                break;
            case 2:
                sendDisable(player);
                break;
            case 3:
                ByteBuilder builder = new BukkitByteBuilder(Constants.ENFORCE_LIST_SKILL);
                builder.writeInt(SkillAPI.getPlayerAccountData(player).getActiveId());
                listSkill(builder,player);
                sendMessage(player,builder);
                break;
        }
    }

    private void listSkill(ByteBuilder builder, Player player){
        boolean has = SkillAPI.hasPlayerData(player) && SkillAPI.getPlayerData(player).hasClass();
        if (has){
            PlayerData data = SkillAPI.getPlayerData(player);
            builder.writeInt(data.getSkills().size());
            for (PlayerSkill ps : data.getSkills()) {
                buildSkill(builder,ps);
            }
        }
    }

    private void buildSkill(ByteBuilder builder, PlayerSkill skill) {
        builder.writeCharSequence(skill.getData().getKey())
                .writeBoolean(skill.isUnlocked())
                .writeBoolean(skill.getData().canCast());
        buildStack(builder,skill);
    }

    private void buildStack(ByteBuilder builder, PlayerSkill skill){
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
                    Method method = Skill.class.getMethod("getIndicator", PlayerSkill.class);
                    stack = (ItemStack) method.invoke(skill.getData(), skill);
                } catch (Exception ignored){}
                break;
            default:
                stack = null;
                break;
        }
        buildStack(builder, stack);
    }

    private void buildStack(ByteBuilder builder, ItemStack stack){
        if (stack == null) {
            builder.writeInt(0).writeShort((short) 0).writeInt(0).writeInt(0);
        } else {
            builder.writeInt(stack.getData().getItemType().getId())
                    //.writeInt(stack.getAmount())
                    .writeShort(stack.getDurability());
            ItemMeta meta = stack.getItemMeta();
            if (meta == null) {
                builder.writeCharSequence("").writeInt(0);
            } else {
                builder.writeCharSequence(meta.getDisplayName()).writeCharSequenceList(meta.getLore());
            }
        }
    }

    public void sendUpdateSkill(Player player, String key){
        int v = 0;
        if (checkValid(player)) v += 1;
        if (checkClient(player)) v += 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("§cRequest send updateSkill to a disabled client");
                break;
            case 2:
                sendDisable(player);
                break;
            case 3:
                ByteBuilder builder = new BukkitByteBuilder(Constants.UPDATE_SKILL);
                PlayerSkill skill = SkillAPI.getPlayerData(player).getSkill(key);
                boolean exist = skill != null;
                builder.writeBoolean(exist);
                if (exist){
                    builder.writeBoolean(skill.isUnlocked()).writeBoolean(skill.getData().canCast());
                    buildStack(builder,skill);
                }
                sendMessage(player,builder);
                break;
        }
    }

    public void sendEnforceUpdateSkill(Player player, String key){
        int v = 0;
        if (checkValid(player)) v += 1;
        if (checkClient(player)) v += 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("§cRequest send enforceUpdateSkill to a disabled client");
                break;
            case 2:
                sendDisable(player);
                break;
            case 3:
                PlayerSkill skill = SkillAPI.getPlayerData(player).getSkill(key);
                boolean exist = skill != null;
                if (exist){
                    ByteBuilder builder = new BukkitByteBuilder(Constants.ENFORCE_UPDATE_SKILL);
                    builder.writeInt(SkillAPI.getPlayerAccountData(player).getActiveId());
                    buildSkill(builder,skill);
                    sendMessage(player,builder);
                }
                break;
        }
    }

    public void sendCast(Player player, String key, boolean suc, byte code){
        int v = 0;
        if (checkValid(player)) v += 1;
        if (checkClient(player)) v+= 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("§cRequest send cast to a disabled client");
                break;
            case 2:
                sendDisable(player);
                break;
            case 3:
                ByteBuilder builder = new BukkitByteBuilder(Constants.CAST);
                builder.writeCharSequence(key);
                builder.writeBoolean(SkillAPI.getPlayerData(player).hasSkill(key));
                builder.writeBoolean(suc);
                builder.write(code);
                sendMessage(player,builder);
                break;
        }
    }

    public void sendAllDisable(){
        Player[] players = VersionManager.getOnlinePlayers();
        for (Player player : players){
            if (checkClient(player)){
                sendDisable(player);
            }
        }
    }

    public void sendDisable(Player player){
        sendMessage(player,new BukkitByteBuilder(Constants.DISABLE));
        manager.get(player).disable();
    }

    public void sendCoolDown(Player player){
        int v = 0;
        if (checkValid(player)) v += 1;
        if (checkClient(player)) v+= 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("§cRequest send cooldown to a disabled client");
                break;
            case 2:
                sendDisable(player);
                break;
            case 3:
                PlayerData data = SkillAPI.getPlayerData(player);
                PlayerBar bar = PlayerBar.get(player);
                if (bar.size() == 0) return;
                ByteBuilder builder = new BukkitByteBuilder(Constants.COOLDOWN);
                ArrayList<String> list = new ArrayList<>(9);
                for (int i = 0; i < 9 ; i++){
                    String key = bar.getSkill(i);
                    if (!list.contains(key) && key != null && !key.isEmpty() && data.hasSkill(key)) list.add(key);
                }
                builder.writeLong(System.currentTimeMillis())
                        .writeInt(list.size());
                for (String key : list){
                    PlayerSkill skill = data.getSkill(key);
                    builder.writeCharSequence(key)
                            .writeInt(skill.getCooldown());
                }
                sendMessage(player,builder);
                break;
        }
    }

    public void sendAccount(Player player){
        int v = 0;
        if (checkValid(player)) v += 1;
        if (checkClient(player)) v+= 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("§cRequest send account to a disabled client");
                break;
            case 2:
                sendDisable(player);
                break;
            case 3:
                ByteBuilder builder = new BukkitByteBuilder(Constants.ACCOUNT);
                builder.writeInt(SkillAPI.getPlayerAccountData(player).getActiveId())
                        .writeInt(SkillAPI.getPlayerData(player).getSkills().size());
                sendMessage(player,builder);
                break;
        }
    }

    public void sendAddSkill(Player p) {
        int v = 0;
        if (checkValid(p)) v += 1;
        if (checkClient(p)) v += 2;
        switch (v){
            case 0:
            case 1:
                logger.warning("§cRequest send account to a disabled client");
                break;
            case 2:
                sendDisable(p);
                break;
            case 3:
                ByteBuilder builder = new BukkitByteBuilder(Constants.ADD_SKILL);
                builder.writeInt(SkillAPI.getPlayerAccountData(p).getActiveId())
                        .writeInt(SkillAPI.getPlayerData(p).getSkills().size());
                sendMessage(p,builder);
                break;
        }
    }

    public void sendClearClientList(Player p) {
        ByteBuilder builder = new BukkitByteBuilder(Constants.ENFORCE_LIST_SKILL);
        builder.writeInt(SkillAPI.getPlayerAccountData(p).getActiveId());
        builder.writeInt(0);
        sendMessage(p, builder);
        builder = new BukkitByteBuilder(Constants.LIST_BAR);
        builder.writeBoolean(true).writeInt(0);
        sendMessage(p, builder);
    }

    public void sendBarList(Player player){
        if (!checkValid(player) || !checkClient(player)) return;

        ByteBuilder builder = new BukkitByteBuilder(Constants.LIST_BAR);
        PlayerBar bar = PlayerBar.get(player);
        PlayerData data = SkillAPI.getPlayerData(player);
        boolean exist = !bar.isEmpty() && data != null;
        builder.writeBoolean(exist);
        if (exist){
            HashMap<Integer, String> map = new HashMap<>(9);
            for (Integer key : bar.keys()){
                if (data.hasSkill(bar.getSkill(key))) map.put(key, bar.getSkill(key));
            }
            if (map.size() != bar.size()) bar.setBar(SkillAPI.getPlayerAccountData(player).getActiveId(), map);
            builder.writeInt(map.size());
            for (HashMap.Entry<Integer, String> entry : map.entrySet()){
                builder.writeInt(entry.getKey()).writeCharSequence(entry.getValue());
            }
        }
        sendMessage(player, builder);
    }

    private byte partId = 0;
    private void sendMessage(Player player, ByteBuilder builder){
        for (byte[] data : builder.build(partId++)) player.sendPluginMessage(plugin,Constants.CHANNEL_NAME,data);
        if (partId == Byte.MAX_VALUE) partId = 0;
    }

    private boolean checkValid(Player player){
        return SkillAPI.isLoaded()
                && player != null
                && player.isOnline()
                && SkillAPI.getSettings().isWorldEnabled(player.getWorld())
                && SkillAPI.hasPlayerData(player)
                && !SkillAPI.getPlayerData(player).getSkills().isEmpty();
    }

    private boolean checkClient(Player player){
        return checkClient(manager.get(player));
    }

    private boolean checkClient(ClientData manager){
        return manager != null && manager.getStatus() == ClientStatus.Enabled;
    }
}
