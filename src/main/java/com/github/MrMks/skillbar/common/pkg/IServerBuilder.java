package com.github.MrMks.skillbar.common.pkg;

import com.github.MrMks.skillbar.common.ByteBuilder;

import java.util.UUID;

public interface IServerBuilder {
    ByteBuilder build(UUID uuid);
    boolean isBuildable(UUID uuid);
}
