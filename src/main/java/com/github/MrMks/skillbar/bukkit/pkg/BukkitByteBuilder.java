package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import io.netty.buffer.ByteBuf;
import org.bukkit.plugin.messaging.MessageTooLargeException;
import org.bukkit.plugin.messaging.Messenger;

public class BukkitByteBuilder extends ByteBuilder {
    public BukkitByteBuilder(byte header) {
        super(header);
    }

    @Override
    public byte[][] build(byte partId) {
        ByteBuf buf = getBuf();
        if (buf.readableBytes() / Messenger.MAX_MESSAGE_SIZE + 1 > Byte.MAX_VALUE)
            throw new MessageTooLargeException();
        else {
            byte size = (byte) (buf.readableBytes() / Messenger.MAX_MESSAGE_SIZE);
            byte[][] dst = new byte[size + 1][];
            for (byte index = 0; index < size + 1; index++){
                byte[] part = new byte[index == size ? buf.readableBytes() + 4 : Messenger.MAX_MESSAGE_SIZE];
                part[0] = Constants.DISCRIMINATOR;//discriminator
                part[1] = partId;
                part[2] = (byte) (size + 1);
                part[3] = (byte) (index + 1);
                buf.readBytes(part,4,part.length - 4);
                dst[index] = part;
            }
            return dst;
        }
    }

    @Override
    public ByteBuf buildBuf() {
        throw new UnsupportedOperationException();
    }
}
