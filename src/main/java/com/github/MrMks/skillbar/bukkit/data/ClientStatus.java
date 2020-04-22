package com.github.MrMks.skillbar.bukkit.data;

import com.github.MrMks.skillbar.bukkit.BlackList;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientStatus implements IClientStatus {
    private final UUID uid;
    private boolean discovered = false;
    private boolean justBlock = false;
    private EnumStatus status = EnumStatus.Discovering;
    private final Set<Integer> cacheSet = new HashSet<>();
    private int acc = -1;

    //private ClientBar bar;
    public ClientStatus(UUID uuid){
        this.uid = uuid;
    }

    public void discover(){
        discovered = true;
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

    public boolean canDisableOnBlocked(){
        if (justBlock){
            justBlock = false;
            return true;
        }
        return false;
    }

    // will never work before discover invoked
    public void enable(){
        if (!discovered) return;
        status = EnumStatus.Enabled;
    }

    public void disable(){
        status = EnumStatus.Disabled;
    }

    public boolean isEnabled(){
        return status == EnumStatus.Enabled;
    }

    public boolean isDisabled(){
        return status == EnumStatus.Disabled;
    }

    private int timesInSeconds = 0;
    private long timeLastUpdate = System.currentTimeMillis();
    public void receive(){
        long now = System.currentTimeMillis();
        if (now - timeLastUpdate >= 1000){
            timesInSeconds = 0;
            timeLastUpdate = now;
        }
        timesInSeconds += 1;
        //if (timesInSeconds > 20) block();
    }

    public void receiveBad(){
        long now = System.currentTimeMillis();
        if (now - timeLastUpdate >= 1000){
            timesInSeconds = 0;
            timeLastUpdate = now;
        }
        timesInSeconds += 3;
        //if (timesInSeconds > 20) block();
    }

    @Override
    public void cache(int id) {
        cacheSet.add(id);
    }

    @Override
    public void cleanCache(int id) {
        cacheSet.remove(id);
    }

    @Override
    public boolean isCached(int id) {
        return cacheSet.contains(id);
    }

    @Override
    public void setClientAccount(int account) {
        acc = account;
    }

    @Override
    public int getClientAccount() {
        return acc;
    }
}