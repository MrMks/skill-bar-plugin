package com.github.MrMks.skillbar.bukkit.data;

import com.github.MrMks.skillbar.bukkit.BlackList;
import com.github.MrMks.skillbar.bukkit.condition.Condition;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.UUID;

public class ClientStatus {
    private UUID uid;
    private boolean discovered = false;
    private boolean justBlock = false;
    private EnumStatus status = EnumStatus.Discovering;
    private Condition condition = null;
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
        status = EnumStatus.Enabled;
    }

    public void disable(){
        status = EnumStatus.Disabled;
    }

    public boolean isEnable(){
        return status == EnumStatus.Enabled;
    }

    public boolean isDisable(){
        return status == EnumStatus.Disabled;
    }

    public Optional<Condition> getCondition(){
        return Optional.ofNullable(condition);
    }

    public void setCondition(Condition condition){
        this.condition = condition;
    }

    public void leaveCondition(){
        condition = null;
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
        if (timesInSeconds > 20) block();
    }

    public void onReceiveBad(){
        long now = System.currentTimeMillis();
        if (now - timeLastUpdate >= 1000){
            timesInSeconds = 0;
            timeLastUpdate = now;
        }
        timesInSeconds += 3;
        if (timesInSeconds > 20) block();
    }
}