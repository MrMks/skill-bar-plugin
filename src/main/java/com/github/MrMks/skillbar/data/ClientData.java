package com.github.MrMks.skillbar.data;

import com.github.MrMks.skillbar.pkg.PackageHandler;
import com.github.MrMks.skillbar.pkg.PluginSender;

import java.util.UUID;

public class ClientData {
    private ClientStatus status;
    private ClientBar bar;
    private PackageHandler handler;
    private PluginSender sender;
    public ClientData(UUID uuid){
        status = new ClientStatus(uuid);
        bar = new ClientBar(uuid);
        sender = new PluginSender(uuid);
        handler = new PackageHandler(status, bar, sender);
    }

    public ClientStatus getStatus() {
        return status;
    }

    public ClientBar getBar() {
        return bar;
    }

    public PackageHandler getHandler() {
        return handler;
    }

    public PluginSender getSender() {
        return sender;
    }
}
