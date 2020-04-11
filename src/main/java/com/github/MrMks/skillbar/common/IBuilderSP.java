package com.github.MrMks.skillbar.common;

import java.util.List;
import java.util.Map;

public interface IBuilderSP {
    void buildDiscover(ByteBuilder builder, int version);
    void buildSetting(ByteBuilder builder, int maxBarSize);
    void buildEnable(ByteBuilder builder, int active, int size);
    void buildAccount(ByteBuilder builder, int active, int size);
    void buildCleanUp(ByteBuilder builder, int active);
    void buildDisable(ByteBuilder builder);

    void buildListSkill(ByteBuilder builder, List<SkillInfo> aList, List<String> reList);
    void buildEnforceListSkill(ByteBuilder builder, List<SkillInfo> list);
    void buildUpdateSkill(ByteBuilder builder, SkillInfo info);
    void buildEnforceUpdateSkill(ByteBuilder builder, SkillInfo info);
    void buildAddSkill(ByteBuilder builder, int active, int size);
    void buildListBar(ByteBuilder builder, Map<Integer, String> map);

    void buildCast(ByteBuilder builder, String key, boolean exist, boolean suc, byte code);
    void buildCoolDown(ByteBuilder builder, Map<String, Integer> map);
}
