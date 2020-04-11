package com.github.MrMks.skillbar.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ByteDecoder {
    private Charset utf8 = StandardCharsets.UTF_8;
    private ByteBuf buf;
    private byte header;
    public ByteDecoder(byte[] bytes) throws IndexOutOfBoundsException{
        buf = PooledByteBufAllocator.DEFAULT.buffer(bytes.length - 1);
        buf.writeBytes(bytes,1,bytes.length - 1);
        header = buf.readByte();
    }

    public ByteDecoder(ByteBuf buf){
        this.buf = buf;
        header = buf.readByte();
    }

    public byte getHeader(){
        return header;
    }

    public byte read() throws IndexOutOfBoundsException{
        return buf.readByte();
    }

    public short readShort() throws IndexOutOfBoundsException{
        return buf.readShort();
    }

    public int readInt() throws IndexOutOfBoundsException{
        return buf.readInt();
    }

    public long readLong() throws IndexOutOfBoundsException{
        return buf.readLong();
    }

    public boolean readBoolean() throws IndexOutOfBoundsException{
        return buf.readBoolean();
    }

    public CharSequence readCharSequence() throws IndexOutOfBoundsException{
        return buf.readCharSequence(readInt(),utf8);
    }

    public List<CharSequence> readCharSequenceList() throws IndexOutOfBoundsException{
        List<CharSequence> list = new ArrayList<>();
        int size = readInt();
        for (int i = 0; i < size; i++){
            list.add(readCharSequence());
        }
        return list;
    }

    public SkillInfo readSkillInfo() throws IndexOutOfBoundsException{
        String key = readCharSequence().toString();
        boolean isUnlock = readBoolean();
        boolean canCast = readBoolean();
        int itemId = readInt();
        short damage = readShort();
        String display = readCharSequence().toString();
        List<CharSequence> lore = readCharSequenceList();
        return new SkillInfo(key,isUnlock,canCast,itemId,damage,display,lore);
    }

    public List<SkillInfo> readSkillInfoList() throws IndexOutOfBoundsException{
        int size = readInt();
        ArrayList<SkillInfo> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++){
            list.add(readSkillInfo());
        }
        return list;
    }
}
