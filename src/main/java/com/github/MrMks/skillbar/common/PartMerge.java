package com.github.MrMks.skillbar.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.HashMap;

public class PartMerge {
    private byte reqId;
    private byte size;
    private byte next = 1;
    private ByteBuf mBuf = PooledByteBufAllocator.DEFAULT.buffer();
    private HashMap<Byte, ByteBuf> map = new HashMap<>();

    public PartMerge(byte reqId, byte size){
        this.reqId = reqId;
        this.size = size;
    }

    public void addPart(ByteBuf buf){
        if (buf != null) {
            if (buf.readByte() == reqId && buf.readByte() == size){
                byte index = buf.readByte();
                if (index == next){
                    readBytes(buf);
                    next ++;
                } else {
                    map.put(index,buf);
                }
            }
        }
        while (map.containsKey(next)){
            buf = map.remove(next);
            readBytes(buf);
            next++;
        }
    }

    private void readBytes(ByteBuf buf){
        while (buf.readableBytes() > 0){
            if (mBuf.writableBytes() == 0) mBuf.capacity(mBuf.capacity() + buf.readableBytes());
            buf.readBytes(mBuf, Math.min(mBuf.writableBytes(), buf.readableBytes()));
        }
    }

    public boolean isComplete(){
        return next > size;
    }

    public ByteBuf getFullPackage(){
        mBuf.readerIndex(0);
        return mBuf;
    }
}
