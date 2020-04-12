package com.github.MrMks.skillbar.common.pkg;

import com.github.MrMks.skillbar.common.ByteAllocator;
import com.github.MrMks.skillbar.common.ByteBuilder;

import java.util.List;
import java.util.Map;

public interface IBuilderCP {
    ByteBuilder buildDiscover(ByteAllocator allocator);

    ByteBuilder buildListSkill(ByteAllocator allocator, List<String> list);
    ByteBuilder buildUpdateSkill(ByteAllocator allocator, String key);
    ByteBuilder buildListBar(ByteAllocator allocator);
    ByteBuilder buildSaveBar(ByteAllocator allocator, Map<Integer, String> map);

    ByteBuilder buildCast(ByteAllocator allocator, String key);
}
