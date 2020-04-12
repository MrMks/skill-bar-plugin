package com.github.MrMks.skillbar.pkg;

import com.github.MrMks.skillbar.common.ByteAllocator;
import com.github.MrMks.skillbar.common.ByteBuilder;

public class BukkitByteAllocator implements ByteAllocator {
    public static ByteAllocator DEFAULT = new BukkitByteAllocator();
    @Override
    public ByteBuilder build(byte header) {
        return new BukkitByteBuilder(header);
    }
}
