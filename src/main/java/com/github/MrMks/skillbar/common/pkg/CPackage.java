package com.github.MrMks.skillbar.common.pkg;

import com.github.MrMks.skillbar.common.*;
import com.github.MrMks.skillbar.common.handler.IServerHandler;

import static com.github.MrMks.skillbar.common.Constants.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPackage {
    public static Builder BUILDER = new Builder();
    public static Decoder DECODER = new Decoder();
    public static class Builder implements IBuilderCP {
        private Builder(){}
        @Override
        public ByteBuilder buildDiscover(ByteAllocator allocator) {
            return allocator.build(DISCOVER);
        }

        @Override
        public ByteBuilder buildListSkill(ByteAllocator allocator, List<String> list) {
            return allocator.build(LIST_SKILL).writeCharSequenceList(list);
        }

        @Override
        public ByteBuilder buildUpdateSkill(ByteAllocator allocator, String key) {
            return allocator.build(UPDATE_SKILL).writeCharSequence(key);
        }

        @Override
        public ByteBuilder buildListBar(ByteAllocator allocator) {
            return allocator.build(LIST_BAR);
        }

        @Override
        public ByteBuilder buildSaveBar(ByteAllocator allocator, Map<Integer, String> map) {
            ByteBuilder builder = allocator.build(SAVE_BAR).writeInt(map.size());
            for (Map.Entry<Integer, String> entry : map.entrySet()){
                builder.writeInt(entry.getKey()).writeCharSequence(entry.getValue());
            }
            return builder;
        }

        @Override
        public ByteBuilder buildCast(ByteAllocator allocator, String key) {
            return allocator.build(CAST).writeCharSequence(key);
        }
    }
    public static class Decoder implements IDecoderCP {
        private Decoder(){}
        @Override
        public void decodeDiscover(IServerHandler handler, ByteDecoder decoder) {
            handler.onDiscover();
        }

        @Override
        public void decodeListSkill(IServerHandler handler, ByteDecoder decoder) {
            handler.onListSkill(decoder.readCharSequenceList());
        }

        @Override
        public void decodeUpdateSkill(IServerHandler handler, ByteDecoder decoder) {
            handler.onUpdateSkill(decoder.readCharSequence());
        }

        @Override
        public void decodeListBar(IServerHandler handler, ByteDecoder decoder) {
            handler.onListBar();
        }

        @Override
        public void decodeSaveBar(IServerHandler handler, ByteDecoder decoder) {
            int size = decoder.readInt();
            Map<Integer, CharSequence> map = new HashMap<>();
            for (int i = 0; i < size; i++){
                int index = decoder.readInt();
                CharSequence key = decoder.readCharSequence();
                map.put(index,key);
            }
            handler.onSaveBar(map);
        }

        @Override
        public void decodeCast(IServerHandler handler, ByteDecoder decoder) {
            handler.onCast(decoder.readCharSequence());
        }
    }
}
