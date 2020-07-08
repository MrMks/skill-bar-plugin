package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.data.ClientStatus;
import com.github.MrMks.skillbar.bukkit.data.UserConfig;

/**
 * Make LogicHandler take place of EventHandler
 */
public interface LogicHandler {
    void onJoin(UserConfig userConfig, ClientStatus status);
    void onDiscovered(UserConfig userConfig, ClientStatus status);
    void onListSkill();
    void onChangeWorldToEnable(UserConfig userConfig, ClientStatus status);
    void onChangeWorldToDisable(UserConfig userConfig, ClientStatus status);
    void onChangeAccount(UserConfig userConfig, ClientStatus status);
}
