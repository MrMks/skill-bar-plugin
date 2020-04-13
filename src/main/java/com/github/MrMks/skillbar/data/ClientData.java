package com.github.MrMks.skillbar.data;

import com.github.MrMks.skillbar.EventHandler;
import com.github.MrMks.skillbar.common.handler.IServerHandler;
import com.github.MrMks.skillbar.pkg.PackageHandler;
import com.github.MrMks.skillbar.pkg.PluginSender;

import java.util.UUID;

public class ClientData {
    private UUID uuid;
    private ClientStatus status;
    private ClientBar bar;
    private PluginSender sender;
    private IServerHandler handler;
    private EventHandler eventHandler;
    public ClientData(UUID uuid){
        this.uuid = uuid;
        status = new ClientStatus(uuid);
        bar = new ClientBar(uuid);
        sender = new PluginSender(uuid);
        handler = new PackageHandler(uuid, status, bar, sender);
        eventHandler = new EventHandler(uuid, status, bar, sender);
    }

    public UUID getUid(){
        return uuid;
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
    }
}
