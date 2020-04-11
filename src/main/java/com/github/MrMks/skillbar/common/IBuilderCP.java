package com.github.MrMks.skillbar.common;

import java.util.List;
import java.util.Map;

public interface IBuilderCP {
    void buildDiscover(ByteBuilder builder);

    void buildListSkill(ByteBuilder builder, List<String> list);
    void buildUpdateSkill(ByteBuilder builder, String key);
    void buildListBar(ByteBuilder builder);
    void buildSaveBar(ByteBuilder builder, Map<Integer, String> map);

    void buildCast(ByteBuilder builder, String key);
}
