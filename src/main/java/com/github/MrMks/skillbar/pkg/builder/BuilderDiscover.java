package com.github.MrMks.skillbar.pkg.builder;

import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.pkg.IServerBuilder;
import com.github.MrMks.skillbar.pkg.BukkitByteBuilder;

import java.util.UUID;

public class BuilderDiscover implements IServerBuilder {
    @Override
    public ByteBuilder build(UUID uuid) {
        return new BukkitByteBuilder(Constants.DISCOVER)
                .writeInt(Constants.VERSION); // version
    }

    @Override
    public boolean isBuildable(UUID uuid) {
        return true;
    }
}
