package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.bukkit.LogicHandler;
import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.common.handler.IServerHandler;

import java.util.List;
import java.util.Map;

public class PackageHandler implements IServerHandler {

    private LogicHandler handler;
    private ClientData data;
    public PackageHandler(ClientData data, LogicHandler handler){
        this.data = data;
        this.handler = handler;
    }

    @Override
    public void onDiscover() {
        handler.onDiscover(data);
    }

    @Override
    public void onListSkill(List<String> keys) {
        handler.onListSkill(data, keys);
    }

    @Override
    public void onUpdateSkill(String key) {
        handler.onUpdateSkill(data, key);
    }

    @Override
    public void onListBar() {
        handler.onListBar(data);
    }

    @Override
    public void onSaveBar(Map<Integer, String> map) {
        handler.onSaveBar(data, map);
    }

    @Override
    public void onCast(String key) {
        handler.onCast(data, key);
    }
}
