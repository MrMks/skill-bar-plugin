package com.github.MrMks.skillbar.data;

import java.util.UUID;

public class ClientData {
    private UUID uid;
    private boolean discovered = false;
    private boolean blocked = false;
    private boolean unload = false;
    private ClientStatus status = ClientStatus.Discovering;
    private PlayerBar bar;
    ClientData(UUID uuid){
        this.uid = uuid;
    }

    public UUID getUid(){
        return uid;
    }

    public void discover(){
        discovered = true;
        bar = new PlayerBar(uid);
    }

    public boolean isDiscovered() {
        return discovered;
    }

    public void block(){
        blocked = true;
    }

    public void unblock(){
        blocked = false;
    }

    public boolean isBlocked(){
        return blocked;
    }

    // will never work before discover invoked
    public void enable(){
        if (!discovered) return;
        status = ClientStatus.Enabled;
    }

    public void disable(){
        status = ClientStatus.Disabled;
    }

    public void unload(){
        //PlayerManager.remove(uid);
        unload = true;
    }

    public boolean isUnload(){
        return unload;
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
}