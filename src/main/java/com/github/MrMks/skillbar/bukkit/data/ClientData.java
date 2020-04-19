package com.github.MrMks.skillbar.bukkit.data;

import com.github.MrMks.skillbar.bukkit.EventHandler;
import com.github.MrMks.skillbar.bukkit.condition.ConditionData;
import com.github.MrMks.skillbar.common.handler.IServerHandler;
import com.github.MrMks.skillbar.bukkit.pkg.PackageHandler;
import com.github.MrMks.skillbar.bukkit.pkg.PluginSender;

import java.util.UUID;

public class ClientData {
    private UUID uuid;
    private ClientStatus status;
    private ClientBar bar;
    private PluginSender sender;
    private IServerHandler handler;
    private EventHandler eventHandler;
    private ConditionData conditionData;
    public ClientData(UUID uuid){
        this.uuid = uuid;
        status = new ClientStatus(uuid);
        bar = new ClientBar(uuid);
        conditionData = new ConditionData();
        sender = new PluginSender(uuid);
        handler = new PackageHandler(uuid, status, bar, conditionData, sender);
        eventHandler = new EventHandler(uuid, status, bar, conditionData, sender);
    }

    public ClientStatus getStatus() {
        return status;
    }

    public IServerHandler getPackageHandler() {
        return handler;
    }

    public EventHandler getEventHandler(){
        return eventHandler;
    }

    public void save(){
        bar.saveToFile();
    }
    public void clean(){
        uuid = null;
        status = null;
        bar = null;
        sender = null;
        handler = null;
        eventHandler = null;
        conditionData = null;
    }
}
