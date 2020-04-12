package com.github.MrMks.skillbar.data;

import com.github.MrMks.skillbar.pkg.PackageHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientManager {
    private final Map<UUID, ClientStatus> map = new HashMap<>();
    public ClientStatus get(Player player){
        return player != null ? get(player.getUniqueId()) : null;
    }

    public ClientStatus get(UUID player){
        if (player != null && !map.containsKey(player)) map.put(player, new ClientStatus(player));
        return map.getOrDefault(player,null);
    }

    public void unload(UUID uuid){
        ClientStatus data = map.remove(uuid);
        if (data != null){
            data.save();
            data.clean();
        }
    }

    public void clearSaveAll(){
        for (ClientStatus data : map.values()){
            data.save();
            data.clean();
        }
        map.clear();
    }
}