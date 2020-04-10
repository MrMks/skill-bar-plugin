package com.github.MrMks.skillbar.pkg.builder;

import com.github.MrMks.skillbar.Setting;
import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.pkg.IServerBuilder;
import com.github.MrMks.skillbar.pkg.BukkitByteBuilder;

import java.util.UUID;

public class BuilderSetting implements IServerBuilder {
    @Override
    public ByteBuilder build(UUID uuid) {
        Setting setting = Setting.getInstance();

        return new BukkitByteBuilder(Constants.SETTING)
                .writeInt(setting.getBarMaxLine());
    }

    @Override
    public boolean isBuildable(UUID uuid) {
        return true;
    }
}
