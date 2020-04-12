package com.github.MrMks.skillbar.pkg;

import com.github.MrMks.skillbar.common.ByteAllocator;
import com.github.MrMks.skillbar.common.ByteBuilder;

public class BukkitByteAllocator implements ByteAllocator {
    @Override
    public ByteBuilder build(byte header) {
        return new BukkitByteBuilder(header);
    }
}
