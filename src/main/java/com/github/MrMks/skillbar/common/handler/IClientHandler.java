package com.github.MrMks.skillbar.common.handler;

import com.github.MrMks.skillbar.common.SkillInfo;

import java.util.List;
import java.util.Map;

public interface IClientHandler {
    void onDiscover(int version);
    void onSetting(int maxSize);
    void onEnable(int activeId, int skillSize);
    void onAccount(int activeId, int skillSize);
    void onDisable();

    void onListSkill(List<SkillInfo> aList, List<String> reList);
    void onEnforceListSkill(List<SkillInfo> list);
    void onUpdateSkill(SkillInfo info);
    void onEnforceUpdateSKill(SkillInfo info);
    void onListBar(Map<Integer, String> map);

    void onCast(String key, boolean exist, boolean suc, byte code);
    void onCoolDown(Map<String, Integer> map);
}
