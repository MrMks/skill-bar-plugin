package com.github.MrMks.skillbar.bukkit.data;

import com.github.MrMks.skillbar.bukkit.EventHandler;
import com.github.MrMks.skillbar.bukkit.pkg.PackageHandler;
import com.github.MrMks.skillbar.bukkit.pkg.PluginSender;
import com.github.MrMks.skillbar.common.handler.IServerHandler;

import java.util.UUID;

public class ClientData {
    private ClientStatus status;
    private ClientAccounts accounts;
    private PluginSender sender;
    private IServerHandler handler;
    private EventHandler eventHandler;

    public ClientData(UUID uuid){
        status = new ClientStatus(uuid);
        sender = new PluginSender(uuid);
        accounts = new ClientAccounts(uuid);
        handler = new PackageHandler(uuid, status, accounts, sender);
        eventHandler = new EventHandler(uuid, status, accounts, sender);
    }

    public IClientStatus getStatus() {
        return status;
    }

    public IServerHandler getPackageHandler() {
        return handler;
    }

    public EventHandler getEventHandler(){
        return eventHandler;
    }

    public void save(){
        accounts.saveToDisk();
    }

    public void clean(){
        status = null;
        sender = null;
        accounts = null;
        handler = null;
        eventHandler = null;
    }
}
