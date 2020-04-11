package com.github.MrMks.skillbar.common.handler;

import com.github.MrMks.skillbar.common.SkillInfo;

import java.util.List;
import java.util.Map;

public interface IClientHandler {
    void onDiscover(int version);
    void onSetting(int maxSize);
    void onEnable(int activeId, int skillSize);
    void onAccount(int activeId, int skillSize);
    void onCleanUp(int activeId);
    void onDisable();

    void onListSkill(List<SkillInfo> aList, List<CharSequence> reList);
    void onEnforceListSkill(List<SkillInfo> list);
    void onUpdateSkill(SkillInfo info);
    void onEnforceUpdateSKill(SkillInfo info);
    void onAddSkill(int activeId, int skillSize);
    void onListBar(Map<Integer, CharSequence> map);

    void onCast(CharSequence key, boolean exist, boolean suc, byte code);
    void onCoolDown(Map<CharSequence, Integer> map);
}
