package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.bukkit.PlayerBar;
import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import com.github.MrMks.skillbar.bukkit.manager.ClientStatus;
import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.Constants;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PackageListener implements PluginMessageListener {
    private ClientManager cm;
    private PackageSender sd;
    private Plugin plugin;
    public PackageListener(Plugin plugin, PackageSender sender, ClientManager manager){
        this.cm = manager;
        this.sd = sender;
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (!s.equals(Constants.CHANNEL_NAME) || player == null) return;
        if (cm.getClientStatus(player.getName()) == ClientStatus.Request_Disable){
            sd.sendDisable(player);
            return;
        } else if (cm.getClientStatus(player.getName()) == ClientStatus.Request_Enable){
            cm.setClientStatus(player.getName(), ClientStatus.Enabled);
        }

        try {
            ByteDecoder decoder = new ByteDecoder(bytes);
            switch (decoder.getHeader()){
                case Constants.ENABLE:
                    onDiscover(player);
                    break;
                case Constants.LIST_SKILL:
                    onListSkill(player,decoder);
                    break;
                case Constants.UPDATE_SKILL:
                    onUpdateSkill(player,decoder);
                    break;
                case Constants.CAST:
                    onCast(player,decoder);
                    break;
                case Constants.LIST_BAR:
                    onListBar(player);
                    break;
                case Constants.SAVE_BAR:
                    onSaveBar(player, decoder);
                    break;
                default:
                    break;
            }
        } catch (IndexOutOfBoundsException e){
            Bukkit.getLogger().warning("Bad Package Received");
        } catch (Throwable tr){
            Bukkit.getLogger().severe("Unexpected Error happened while executing client message from player: " + player.getName());
            tr.printStackTrace();
        }
    }

    private void onDiscover(Player player){
        cm.setClientStatus(player.getName(), ClientStatus.Enabled);
    }

    public void onListSkill(Player player, ByteDecoder buf){
        sd.sendListSkill(player, buf.readCharSequenceList());
    }

    public void onUpdateSkill(Player player, ByteDecoder buf){
        String key = buf.readCharSequence().toString();
        sd.sendUpdateSkill(player,key);
    }

    public void onCast(Player player, ByteDecoder buf){
        boolean hasPlayer = SkillAPI.hasPlayerData(player);
        String key = buf.readCharSequence().toString();
        if (hasPlayer){
            PlayerData data = SkillAPI.getPlayerData(player);
            PlayerSkill skill = data.getSkill(key);
            boolean exist = skill != null;
            boolean unlock = exist && skill.isUnlocked();
            boolean cd = exist && skill.isOnCooldown();

            if (!exist) sd.sendCast(player,key, false,Constants.CAST_FAILED_NO_SKILL);
            else if (!unlock) sd.sendCast(player,key,false,Constants.CAST_FAILED_UNLOCK);
            else if (cd) sd.sendCast(player,key, false, Constants.CAST_FAILED_COOLDOWN);
            else {
                boolean suc = data.cast(skill);
                if (suc){
                    sd.sendCast(player,key, true,Constants.CAST_SUCCESS);
                    sd.sendCoolDown(player);
                } else {
                    sd.sendCast(player,key,false,Constants.CAST_FAILED_UNEXPECTED);
                }
            }
        }
    }

    public void onListBar(Player player){
        sd.sendBarList(player);
    }

    public void onSaveBar(Player player, ByteDecoder buf){
        PlayerBar bar = PlayerBar.get(player);
        PlayerData data = SkillAPI.getPlayerData(player);
        int activeId = buf.readInt();
        int size = buf.readInt();
        HashMap<Integer, String> map = new HashMap<>();
        ArrayList<String> removes = new ArrayList<>();
        for (int i = 0; i < size; i++){
            int order = buf.readInt();
            String key = buf.readCharSequence().toString();
            if (data.hasSkill(key)) map.put(order,key);
            else removes.add(key);
        }
        bar.setBar(activeId, map);
        sd.sendRemoveList(player, removes);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, bar::saveToFile);
    }
}
