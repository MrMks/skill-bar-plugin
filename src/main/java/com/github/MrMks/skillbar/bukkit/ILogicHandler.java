package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.data.ClientStatus;
import com.github.MrMks.skillbar.bukkit.data.UserConfig;

/**
 * Make LogicHandler take place of EventHandler
 * There will only one instance of this class
 */
public interface ILogicHandler {
    void onJoin(UserConfig userConfig, ClientStatus status);
    void onExit(UserConfig userConfig, ClientStatus status);
    void onChangeWorld(UserConfig userConfig, ClientStatus status);
    void onChangeAccount(UserConfig userConfig, ClientStatus status);
    void onChangeProfession(UserConfig userConfig, ClientStatus status);
    void onSkillUpgrade(UserConfig userConfig, ClientStatus status);
    void onSkillDowngrade(UserConfig userConfig, ClientStatus status);
    void onDiscovered(UserConfig userConfig, ClientStatus status);
    void onListSkill(UserConfig userConfig, ClientStatus status);
    void onUpdateSkill(UserConfig userConfig, ClientStatus status);
    void onListBar(UserConfig userConfig, ClientStatus status);
    void onSaveBar(UserConfig userConfig, ClientStatus status);
    void onCast(UserConfig userConfig, ClientStatus status);
    void onUpdateCooldown(UserConfig userConfig, ClientStatus status);
}
