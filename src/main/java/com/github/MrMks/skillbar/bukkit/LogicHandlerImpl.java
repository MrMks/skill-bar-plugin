package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.data.ClientStatus;
import com.github.MrMks.skillbar.bukkit.data.UserConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class LogicHandlerImpl implements ILogicHandler {

    private final List<UUID> clients = new LinkedList<>();
    public LogicHandlerImpl(){}

    @Override
    public void onJoin(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onExit(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onChangeWorld(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onChangeAccount(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onChangeProfession(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onSkillUpgrade(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onSkillDowngrade(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onDiscovered(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onListSkill(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onUpdateSkill(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onListBar(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onSaveBar(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onCast(UserConfig userConfig, ClientStatus status) {

    }

    @Override
    public void onUpdateCooldown(UserConfig userConfig, ClientStatus status) {

    }
}
