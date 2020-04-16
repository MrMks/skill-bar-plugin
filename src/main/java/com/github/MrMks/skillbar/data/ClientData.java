package com.github.MrMks.skillbar.data;

import com.github.MrMks.skillbar.BlackList;
import org.bukkit.Bukkit;

import java.util.UUID;

public class ClientData {
    private UUID uid;
    private boolean discovered = false;
    private boolean justBlock = false;
    private ClientStatus status = ClientStatus.Discovering;
    private PlayerBar bar;
    ClientData(UUID uuid){
        this.uid = uuid;
    }

    public UUID getUid(){
        return uid;
    }

    public void startDiscover(){
        discovered = false;
        status = ClientStatus.Discovering;
    }

    public void discover(){
        discovered = true;
        bar = new PlayerBar(uid);
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void block(){
        if (!isBlocked()) {
            BlackList.getInstance().add(Bukkit.getOfflinePlayer(uid).getName());
            justBlock = true;
        }
    }

    public void unblock(){
        BlackList.getInstance().remove(Bukkit.getOfflinePlayer(uid).getName());
        justBlock = false;
    }

    public boolean isBlocked(){
        return BlackList.getInstance().contain(Bukkit.getOfflinePlayer(uid).getName());
    }

    public boolean isSendDisable(){
        if (justBlock){
            justBlock = false;
            return true;
        }
        return false;
    }

    // will never work before discover invoked
    public void enable(){
        if (!discovered) return;
        status = ClientStatus.Enabled;
    }

    public void disable(){
        status = ClientStatus.Disabled;
    }

    /**
     * client status
     * @return <code>ClientStatus.Enabled</code> if enable was invoked, <code>ClientStatus.Disabled</code> otherwise;
     */
    public ClientStatus getStatus() {
        return status;
    }

    private byte index = 0;
    public byte getPackageIndex(){
        if (index == Byte.MAX_VALUE) index = 0;
        return index++;
    }

    public PlayerBar getBar(){
        return bar;
    }

    public void save(){
        if (bar != null) bar.saveToFile();
    }

    public void clean(){
        bar = null;
    }

    private int timesInSeconds = 0;
    private long timeLastUpdate = System.currentTimeMillis();
    public void onReceive(){
        long now = System.currentTimeMillis();
        if (now - timeLastUpdate >= 1000){
            timesInSeconds = 0;
            timeLastUpdate = now;
        }
        timesInSeconds += 1;
        // 暂时禁用频率检查
        // if (timesInSeconds > 20) block();
    }

    public void onReceiveBad(){
        long now = System.currentTimeMillis();
        if (now - timeLastUpdate >= 1000){
            timesInSeconds = 0;
            timeLastUpdate = now;
        }
        timesInSeconds += 3;
        // 暂时禁用频率检查
        // if (timesInSeconds > 20) block();
    }
}