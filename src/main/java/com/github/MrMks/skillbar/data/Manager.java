package com.github.MrMks.skillbar.data;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Manager {
    private final Map<UUID, ClientData> map = new HashMap<>();
    public ClientData get(Player player){
        return player != null ? get(player.getUniqueId()) : null;
    }

    public ClientData get(UUID player){
        if (player != null && !map.containsKey(player)) map.put(player, new ClientData(player));
        return map.getOrDefault(player,null);
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
}