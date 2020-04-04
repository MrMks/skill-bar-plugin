package com.github.MrMks.skillbar.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class ByteBuilder {
    private ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(256);
    private Charset utf8 = StandardCharsets.UTF_8;
    public ByteBuilder(byte header){
        buf.writeByte(header);
    }

    public ByteBuilder write(byte v){
        buf.writeByte(v);
        return this;
    }

    public ByteBuilder writeShort(short v){
        buf.writeShort(v);
        return this;
    }

    public ByteBuilder writeInt(int v){
        buf.writeInt(v);
        return this;
    }

    public ByteBuilder writeLong(long v){
        buf.writeLong(v);
        return this;
    }

    public ByteBuilder writeBoolean(boolean v){
        buf.writeBoolean(v);
        return this;
    }

    public ByteBuilder writeCharSequence(CharSequence v){
        if (v == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(v.toString().getBytes(utf8).length);
            buf.writeCharSequence(v, utf8);
        }
        return this;
    }

    public ByteBuilder writeCharSequenceList(List<? extends CharSequence> v){
        if (v == null) {
            writeInt(0);
        } else {
            writeInt(v.size());
            for (CharSequence c : v){
                writeCharSequence(c);
            }
        }
        return this;
    }

    public abstract byte[][] build(byte partId);

    public abstract ByteBuf buildBuf();

    protected ByteBuf getBuf(){
        return buf;
    }


}
