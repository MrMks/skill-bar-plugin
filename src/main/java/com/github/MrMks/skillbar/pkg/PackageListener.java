package com.github.MrMks.skillbar.pkg;

import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.data.ClientData;
import com.github.MrMks.skillbar.data.ClientStatus;
import com.github.MrMks.skillbar.data.Manager;
import com.github.MrMks.skillbar.data.PlayerBar;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PackageListener implements PluginMessageListener {
    private PackageSender sd;
    private Plugin plugin;
    private Manager manager;
    public PackageListener(Plugin plugin, PackageSender sender, Manager manager){
        this.sd = sender;
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (!s.equals(Constants.CHANNEL_NAME) || player == null) return;
        ClientData m = manager.get(player);
        if (m.getStatus() == ClientStatus.Disabled){
            if (m.isDiscovered()) sd.sendDisable(player);
            return;
        }

        try {
            ByteDecoder decoder = new ByteDecoder(bytes);
            switch (decoder.getHeader()){
                case Constants.DISCOVER:
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
                    Bukkit.getLogger().warning("Undefined package header received from player: " + player.getName());
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
        ClientData data = manager.get(player);
        if (data != null && !data.isDiscovered()){
            data.discover();
            sd.sendEnable(player);
        }
    }

    public void onListSkill(Player player, ByteDecoder buf){
        ClientData data = manager.get(player);
        if (data != null && data.getStatus() == ClientStatus.Enabled){
            sd.sendListSkill(player, buf.readCharSequenceList());
        }
    }

    public void onUpdateSkill(Player player, ByteDecoder buf){
        ClientData data = manager.get(player);
        if (data != null && data.getStatus() == ClientStatus.Enabled){
            String key = buf.readCharSequence().toString();
            sd.sendUpdateSkill(player,key);
        }
    }

    public void onCast(Player player, ByteDecoder buf){
        if (manager.get(player) == null || manager.get(player).getStatus() != ClientStatus.Enabled) return;
        boolean hasPlayer = SkillAPI.hasPlayerData(player);
        String key = buf.readCharSequence().toString();
        if (hasPlayer){
            PlayerData data = SkillAPI.getPlayerData(player);
            //PlayerSkill skill = data.getSkill(key);
            boolean exist = data.hasSkill(key);
            boolean unlock = exist && data.getSkillLevel(key) != 0;
            //boolean cd = exist && skill.isOnCooldown();

            if (!exist) sd.sendCast(player,key, false,Constants.CAST_FAILED_NO_SKILL);
            else if (!unlock) sd.sendCast(player,key,false,Constants.CAST_FAILED_UNLOCK);
            else {
                boolean suc = data.cast(key);
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
        ClientData cData = manager.get(player);
        if (cData != null && cData.getStatus() == ClientStatus.Enabled) sd.sendListBar(player);
    }

    public void onSaveBar(Player player, ByteDecoder buf){
        if (manager.get(player) == null || manager.get(player).getStatus() != ClientStatus.Enabled) return;
        PlayerBar bar = manager.get(player).getBar();
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