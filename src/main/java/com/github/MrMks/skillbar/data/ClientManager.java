package com.github.MrMks.skillbar.data;

import org.bukkit.entity.Player;

import java.util.*;

public class ClientManager {
    private final Map<UUID, ClientData> map = new HashMap<>();

    public void prepare(Player player) {
        if (player != null) prepare(player.getUniqueId());
    }
    public void prepare(UUID uuid) {
        if (uuid != null && !map.containsKey(uuid)) map.put(uuid, new ClientData(uuid));
    }

    public ClientData get(Player player){
        return player != null ? get(player.getUniqueId()) : null;
    }
    public ClientData get(UUID uuid){
        return uuid != null ? map.get(uuid) : null;
    }

    public boolean has(Player player){
        return player != null && has(player.getUniqueId());
    }
    public boolean has(UUID uuid){
        return map.containsKey(uuid);
    }

    public void unload(Player player){
        if (player != null) unload(player.getUniqueId());
    }
    public void unload(UUID uuid){
        ClientData data = map.remove(uuid);
        if (data != null){
            data.save();
            data.clean();
        }
    }

    public void clearSaveAll(){
        for (ClientData data : map.values()){
            data.save();
            data.clean();
        }
        map.clear();
    }

    public Collection<ClientData> getAll(){
        return map.values();
    }
}