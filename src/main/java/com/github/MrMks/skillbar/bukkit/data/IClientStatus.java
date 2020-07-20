package com.github.MrMks.skillbar.bukkit.data;

public interface IClientStatus {
    void discover();
    boolean isDiscovered();

    void enable();
    boolean isEnabled();
    void disable();
    boolean isDisabled();

    void block();
    void unblock();
    boolean isBlocked();
    boolean canDisableOnBlocked();
    void receive();
    void receiveBad();

    void cache(int id);
    void cleanCache(int id);
    boolean isCached(int id);

    void setClientAccount(int account);
    int getClientAccount();

    byte getPackageIndex();
}
