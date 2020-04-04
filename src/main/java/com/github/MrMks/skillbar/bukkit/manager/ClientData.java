package com.github.MrMks.skillbar.bukkit.manager;

import java.util.UUID;

public class ClientData {
    private UUID uid;
    private boolean discovered = false;
    private boolean blocked = false;
    private boolean unload = false;
    private ClientStatus status = ClientStatus.Discovering;
    ClientData(UUID uuid){
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

}
