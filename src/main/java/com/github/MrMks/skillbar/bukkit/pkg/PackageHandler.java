package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.bukkit.LogicHandler;
import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.common.handler.IServerHandler;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;

public class PackageHandler implements IServerHandler {

    private LogicHandler handler;
    private ClientData data;
    public PackageHandler(ClientData data, LogicHandler handler){
        this.data = data;
        this.handler = handler;
    }

    private boolean checkValid(){
        return data != null && data.isValid();
    }

    @Override
    public void onDiscover() {
        handler.onDiscover(data);
    }

    @Override
    public void onListSkill(List<String> keys) {
        if (checkValid()){
            handler.onListSkill(data, keys);
        }
    }

    @Override
    public void onUpdateSkill(String key) {
        if (checkValid()){
            handler.onUpdateSkill(data, key);
        }
    }

    @Override
    public void onListBar() {
        if (checkValid()){
            handler.onListBar(data);
        }
    }

    @Override
    public void onSaveBar(Map<Integer, String> map) {
        if (checkValid()){
            handler.onSaveBar(data, map);
        }
    }

    @Override
    public void onCast(String key) {
        if (checkValid()){
            handler.onCast(data, key);
        }
    }
}
