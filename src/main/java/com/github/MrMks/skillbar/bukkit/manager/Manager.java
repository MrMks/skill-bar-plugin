package com.github.MrMks.skillbar.bukkit.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Manager {
    private final Map<UUID, ClientData> map = new HashMap<>();
    private ClientData empty = new ClientData(UUID.randomUUID()){
        @Override
        public ClientStatus getStatus() {
            return ClientStatus.Disabled;
        }
        @Override
        public void discover() {}
        @Override
        public void block() {}
        @Override
        public void disable() {}
        @Override
        public void enable() {}
        @Override
        public void unload() {}
    };
    public ClientData get(Player player){
        return player != null ? get(player.getUniqueId()) : null;
    }

    public ClientData get(UUID player){
        if (player != null && (!map.containsKey(player) || map.get(player).isUnload())) map.put(player, new ClientData(player));
        return map.get(player);
    }

    public void clearAll() {
        map.clear();
    }
}
