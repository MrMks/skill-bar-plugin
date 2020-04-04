package com.github.MrMks.skillbar.bukkit.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private static final Map<UUID, PlayerManager> map = new HashMap<>();
    public static PlayerManager get(Player player){
        return player != null ? get(player.getUniqueId()) : null;
    }

    public static PlayerManager get(UUID player){
        if (player != null && !map.containsKey(player)) map.put(player, new PlayerManager(player));
        return map.get(player);
    }

    public static void clearAll() {
        map.clear();
    }

    private UUID uid;
    private boolean discovered = false;
    private ClientStatus status = ClientStatus.Discovering;
    private PlayerManager(UUID uuid){
        this.uid = uuid;
    }

    public UUID getUid(){
        return uid;
    }

    public void discover(){
        discovered = true;
    }

    public boolean isDiscovered() {
        return discovered;
    }

    // will never work before discover invoked
    public void enable(){
        if (!discovered) return;
        status = ClientStatus.Enabled;
    }

    public void disable(){
        status = ClientStatus.Disabled;
    }

    public PlayerBar getBar(){
        return PlayerBar.get(uid);
    }

    /**
     * client status
     * @return <code>ClientStatus.Enabled</code> if enable was invoked, <code>ClientStatus.Disabled</code> otherwise;
     */
    public ClientStatus getStatus() {
        return status;
    }
}
