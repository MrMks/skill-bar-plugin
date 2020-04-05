package com.github.MrMks.skillbar.bukkit.data;

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
        if (player != null && (!map.containsKey(player) || map.get(player).isUnload())) map.put(player, new ClientData(player));
        return map.getOrDefault(player,null);
    }

    public void clearAll() {
        map.clear();
    }
}
