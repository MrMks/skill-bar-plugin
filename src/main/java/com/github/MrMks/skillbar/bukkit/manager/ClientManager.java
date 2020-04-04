package com.github.MrMks.skillbar.bukkit.manager;

import java.util.HashMap;

public class ClientManager {
    private static HashMap<String, ClientStatus> map;

    public ClientManager(){
        if (map == null) map = new HashMap<>();
    }

    public ClientStatus getClientStatus(String name){
        return map.getOrDefault(name,ClientStatus.Request_Disable);
    }

    public void setClientStatus(String name, ClientStatus status){
        if (status != null) {
            if (status != ClientStatus.Request_Disable) map.put(name, status);
            else map.remove(name);
        }
    }
}
