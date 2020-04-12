package com.github.MrMks.skillbar.common.pkg;

import com.github.MrMks.skillbar.common.ByteAllocator;
import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.SkillInfo;

import java.util.List;
import java.util.Map;

public interface IBuilderSP {
    ByteBuilder buildDiscover(ByteAllocator allocator, int version);
    ByteBuilder buildSetting(ByteAllocator allocator, int maxBarSize);
    ByteBuilder buildEnable(ByteAllocator allocator, int active, int size);
    ByteBuilder buildAccount(ByteAllocator allocator, int active, int size);
    ByteBuilder buildCleanUp(ByteAllocator allocator, int active);
    ByteBuilder buildDisable(ByteAllocator allocator);

    ByteBuilder buildListSkill(ByteAllocator allocator, List<SkillInfo> aList, List<String> reList);
    ByteBuilder buildEnforceListSkill(ByteAllocator allocator, List<SkillInfo> list);
    ByteBuilder buildUpdateSkill(ByteAllocator allocator, SkillInfo info);
    ByteBuilder buildEnforceUpdateSkill(ByteAllocator allocator, SkillInfo info);
    ByteBuilder buildAddSkill(ByteAllocator allocator, int active, int size);
    ByteBuilder buildListBar(ByteAllocator allocator, Map<Integer, String> map);

    ByteBuilder buildCast(ByteAllocator allocator, String key, boolean exist, boolean suc, byte code);
    ByteBuilder buildCoolDown(ByteAllocator allocator, Map<String, Integer> map);
}
