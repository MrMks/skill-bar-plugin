package com.github.MrMks.skillbar.pkg;

import com.github.MrMks.skillbar.Setting;
import com.github.MrMks.skillbar.data.ClientStatus;
import com.github.MrMks.skillbar.data.EnumStatus;
import com.github.MrMks.skillbar.data.ClientManager;
import com.github.MrMks.skillbar.data.ClientBar;
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
import java.util.*;

@Deprecated
public class PackageSender {

    private Plugin plugin;
    private ClientManager manager;
    private byte itemMethodFlag = 0;
    public PackageSender(Plugin plugin, ClientManager manager){
        this.plugin = plugin;
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

    /*
    public boolean isLoad(){
        return !(itemMethodFlag == 0);
    }

    public void sendDiscover(Player player){
        manager.get(player).startDiscover();
        if (player != null && !manager.get(player).isBlocked()) sendMessage(player, new BukkitByteBuilder(Constants.DISCOVER).writeInt(Constants.VERSION));
    }

    public void sendSetting(Player player){
        if (player != null && !manager.get(player).isBlocked()) {
            Setting setting = Setting.getInstance();
            ByteBuilder builder = new BukkitByteBuilder(Constants.SETTING).writeInt(setting.getBarMaxLine());
            sendMessage(player, builder);
        }
    }

    public void sendEnable(Player player){
        ClientStatus m = manager.get(player);
        if (checkValid(player) && !m.isBlocked()){
            if (m.getStatus() != EnumStatus.Enabled) {
                ByteBuilder builder = new BukkitByteBuilder(Constants.ENABLE);
                builder.writeInt(SkillAPI.getPlayerAccountData(player).getActiveId());
                builder.writeInt(SkillAPI.getPlayerData(player).getSkills().size());
                m.enable();
                sendMessage(player, builder);
            }
        }
    }

    public void sendDisable(Player player){
        if (player != null && !manager.get(player).isBlocked()) {
            manager.get(player).disable();
            sendMessage(player,new BukkitByteBuilder(Constants.DISABLE));
        }
    }

    public void sendListSkill(Player player, List<CharSequence> req){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
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
        }
    }

    public void sendRemoveList(Player player, List<? extends CharSequence> req){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
            ByteBuilder builder = new BukkitByteBuilder(Constants.LIST_SKILL);
            builder.writeInt(0); // add skill list
            builder.writeCharSequenceList(req);
            sendMessage(player,builder);
        }
    }

    public void sendEnforceListSkill(Player player){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
            ByteBuilder builder = new BukkitByteBuilder(Constants.ENFORCE_LIST_SKILL);
            builder.writeInt(SkillAPI.getPlayerAccountData(player).getActiveId());
            listSkill(builder,player);
            sendMessage(player,builder);
        }
    }

    public void sendUpdateSkill(Player player, String key){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
            ByteBuilder builder = new BukkitByteBuilder(Constants.UPDATE_SKILL);
            PlayerSkill skill = SkillAPI.getPlayerData(player).getSkill(key);
            boolean exist = skill != null;
            builder.writeBoolean(exist);
            if (exist){
                builder.writeBoolean(skill.isUnlocked()).writeBoolean(skill.getData().canCast());
                buildStack(builder,skill);
            }
            sendMessage(player,builder);
        }
    }

    public void sendEnforceUpdateSkill(Player player, String key){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
            PlayerSkill skill = SkillAPI.getPlayerData(player).getSkill(key);
            boolean exist = skill != null;
            if (exist){
                ByteBuilder builder = new BukkitByteBuilder(Constants.ENFORCE_UPDATE_SKILL);
                builder.writeInt(SkillAPI.getPlayerAccountData(player).getActiveId());
                buildSkill(builder,skill);
                sendMessage(player,builder);
            }
        }
    }

    public void sendCast(Player player, String key, boolean suc, byte code){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
            ByteBuilder builder = new BukkitByteBuilder(Constants.CAST);
            builder.writeCharSequence(key);
            builder.writeBoolean(suc);
            builder.write(code);
            sendMessage(player,builder);
        }
    }

    public void sendCoolDown(Player player){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
            PlayerData data = SkillAPI.getPlayerData(player);
            ClientBar bar = manager.get(player).getBar();
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
        }
    }

    public void sendAccount(Player player){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
            ByteBuilder builder = new BukkitByteBuilder(Constants.ACCOUNT);
            builder.writeInt(SkillAPI.getPlayerAccountData(player).getActiveId())
                    .writeInt(SkillAPI.getPlayerData(player).getSkills().size());
            sendMessage(player,builder);
        }
    }

    public void sendAddSkill(Player p) {
        if (checkValid(p) && !manager.get(p).isBlocked()) {
            ByteBuilder builder = new BukkitByteBuilder(Constants.ADD_SKILL);
            builder.writeInt(SkillAPI.getPlayerAccountData(p).getActiveId())
                    .writeInt(SkillAPI.getPlayerData(p).getSkills().size());
            sendMessage(p,builder);
        }
    }

    public void sendListBar(Player player){
        if (checkValid(player) && !manager.get(player).isBlocked()) {
            ByteBuilder builder = new BukkitByteBuilder(Constants.LIST_BAR);
            ClientBar bar = manager.get(player).getBar();
            PlayerData data = SkillAPI.getPlayerData(player);
            boolean exist = !bar.isEmpty() && data != null;
            builder.writeBoolean(exist);
            if (exist) {
                HashMap<Integer, String> map = new HashMap<>(9);
                for (Integer key : bar.keys()) {
                    if (data.hasSkill(bar.getSkill(key))) map.put(key, bar.getSkill(key));
                }
                if (map.size() != bar.size()) bar.setBar(SkillAPI.getPlayerAccountData(player).getActiveId(), map);
                builder.writeInt(map.size());
                for (HashMap.Entry<Integer, String> entry : map.entrySet()) {
                    builder.writeInt(entry.getKey()).writeCharSequence(entry.getValue());
                }
            }
            sendMessage(player, builder);
        }
    }

    private void sendMessage(Player player, ByteBuilder builder){
        byte partId = manager.get(player).getPackageIndex();
        for (byte[] data : builder.build(partId)) player.sendPluginMessage(plugin,Constants.CHANNEL_NAME,data);
    }

    public void sendAllDiscover() {
        for (Player player : VersionManager.getOnlinePlayers()){
            if (manager.get(player) != null && !manager.get(player).isDiscovered()) sendDiscover(player);
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

    public void sendClearClientList(Player p) {
        ByteBuilder builder = new BukkitByteBuilder(Constants.CLEAN);
        builder.writeInt(SkillAPI.getPlayerAccountData(p).getActiveId());
        sendMessage(p, builder);
    }

    private boolean checkValid(Player player){
        return SkillAPI.isLoaded()
                && player != null
                && player.isOnline()
                && SkillAPI.getSettings().isWorldEnabled(player.getWorld())
                && SkillAPI.hasPlayerData(player)
                && !SkillAPI.getPlayerData(player).getClasses().isEmpty()
                && !SkillAPI.getPlayerData(player).getSkills().isEmpty();
    }

    private boolean checkClient(Player player){
        return checkClient(manager.get(player));
    }

    private boolean checkClient(ClientStatus manager){
        return manager != null && manager.getStatus() == EnumStatus.Enabled;
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
                    // method used in non-premium skillapi
                    @SuppressWarnings("JavaReflectionMemberAccess")
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
            ItemMeta meta = stack.getItemMeta();
            //noinspection deprecation
            builder.writeInt(stack.getData().getItemType().getId())
                    //.writeInt(stack.getAmount())
                    .writeShort(stack.getDurability());
            if (meta == null) {
                builder.writeCharSequence("")
                        .writeCharSequenceList(Collections.emptyList());
            } else {
                builder.writeCharSequence(meta.getDisplayName())
                        .writeCharSequenceList(meta.getLore());
            }

        }
    }
     */
}