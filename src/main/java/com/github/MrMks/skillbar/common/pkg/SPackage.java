package com.github.MrMks.skillbar.common.pkg;

import com.github.MrMks.skillbar.common.*;
import com.github.MrMks.skillbar.common.handler.IClientHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.MrMks.skillbar.common.Constants.*;

public class SPackage {
    public static class Builder implements IBuilderSP {
        @Override
        public void buildDiscover(ByteBuilder builder, int version) {
            builder.header(DISCOVER).writeInt(version);
        }

        @Override
        public void buildSetting(ByteBuilder builder, int maxBarSize) {
            builder.header(SETTING).writeInt(maxBarSize);
        }

        @Override
        public void buildEnable(ByteBuilder builder, int active, int size) {
            builder.header(ENABLE).writeInt(active).writeInt(size);
        }

        @Override
        public void buildAccount(ByteBuilder builder, int active, int size) {
            builder.header(ACCOUNT).writeInt(active).writeInt(size);
        }

        @Override
        public void buildCleanUp(ByteBuilder builder, int active) {
            builder.header(CLEAN).writeInt(active);
        }

        @Override
        public void buildDisable(ByteBuilder builder) {
            builder.header(DISABLE);
        }

        @Override
        public void buildListSkill(ByteBuilder builder, List<SkillInfo> aList, List<String> reList) {
            builder.header(LIST_SKILL).writeInt(aList.size()).writeSkillInfoList(aList).writeCharSequenceList(reList);
        }

        @Override
        public void buildEnforceListSkill(ByteBuilder builder, List<SkillInfo> list) {
            builder.header(ENFORCE_LIST_SKILL).writeInt(list.size()).writeSkillInfoList(list);
        }

        @Override
        public void buildUpdateSkill(ByteBuilder builder, SkillInfo info) {
            builder.header(UPDATE_SKILL).writeSkillInfo(info);

        }

        @Override
        public void buildEnforceUpdateSkill(ByteBuilder builder, SkillInfo info) {
            builder.header(ENFORCE_UPDATE_SKILL).writeSkillInfo(info);
        }

        @Override
        public void buildAddSkill(ByteBuilder builder, int active, int size) {
            builder.header(ADD_SKILL).writeInt(active).writeInt(size);
        }

        @Override
        public void buildListBar(ByteBuilder builder, Map<Integer, String> map) {
            builder.header(LIST_BAR).writeInt(map.size());
            for (Map.Entry<Integer,String> entry : map.entrySet()) {
                builder.writeInt(entry.getKey()).writeCharSequence(entry.getValue());
            }
        }

        @Override
        public void buildCast(ByteBuilder builder, String key, boolean exist, boolean suc, byte code) {
            builder.header(CAST).writeCharSequence(key).writeBoolean(exist).writeBoolean(suc).write(code);
        }

        @Override
        public void buildCoolDown(ByteBuilder builder, Map<String, Integer> map) {
            builder.write(COOLDOWN).writeInt(map.size());
            for (Map.Entry<String,Integer> entry : map.entrySet()){
                builder.writeCharSequence(entry.getKey()).writeInt(entry.getValue());
            }
        }
    }
    public static class Decoder implements IDecoderSP {
        @Override
        public void decodeDiscover(IClientHandler handler, ByteDecoder decoder) {
            int version = decoder.readInt();
            handler.onDiscover(version);
        }

        @Override
        public void decodeSetting(IClientHandler handler, ByteDecoder decoder) {
            int size = decoder.readInt();
            handler.onSetting(size);
        }

        @Override
        public void decodeEnable(IClientHandler handler, ByteDecoder decoder) {
            int activeId = decoder.readInt();
            int size = decoder.readInt();
            handler.onEnable(activeId,size);
        }

        @Override
        public void decodeAccount(IClientHandler handler, ByteDecoder decoder) {
            int activeId = decoder.readInt();
            int size = decoder.readInt();
            handler.onAccount(activeId,size);
        }

        @Override
        public void decodeCleanUp(IClientHandler handler, ByteDecoder decoder) {
            handler.onCleanUp(decoder.readInt());
        }

        @Override
        public void decodeDisable(IClientHandler handler, ByteDecoder decoder) {
            handler.onDisable();
        }

        @Override
        public void decodeListSkill(IClientHandler handler, ByteDecoder decoder) {
            List<SkillInfo> aList = decoder.readSkillInfoList();
            List<CharSequence> reList = decoder.readCharSequenceList();
            handler.onListSkill(aList, reList);
        }

        @Override
        public void decodeEnforceListSkill(IClientHandler handler, ByteDecoder decoder) {
            List<SkillInfo> list = decoder.readSkillInfoList();
            handler.onEnforceListSkill(list);
        }

        @Override
        public void decodeUpdateSkill(IClientHandler handler, ByteDecoder decoder) {
            SkillInfo info = decoder.readSkillInfo();
            handler.onUpdateSkill(info);
        }

        @Override
        public void decodeEnforceUpdateSkill(IClientHandler handler, ByteDecoder decoder) {
            SkillInfo info = decoder.readSkillInfo();
            handler.onEnforceUpdateSKill(info);
        }

        @Override
        public void decodeAddSkill(IClientHandler handler, ByteDecoder decoder) {
            int active = decoder.readInt();
            int size = decoder.readInt();
            handler.onAddSkill(active,size);
        }

        @Override
        public void decodeListBar(IClientHandler handler, ByteDecoder decoder) {
            int size = decoder.readInt();
            Map<Integer, CharSequence> map = new HashMap<>();
            for (int i = 0; i < size ; i++){
                int index = decoder.readInt();
                CharSequence key = decoder.readCharSequence();
                map.put(index,key);
            }
            handler.onListBar(map);
        }

        @Override
        public void decodeCast(IClientHandler handler, ByteDecoder decoder) {
            CharSequence key = decoder.readCharSequence();
            boolean exist = decoder.readBoolean();
            boolean suc = decoder.readBoolean();
            byte code = decoder.read();
            handler.onCast(key,exist,suc,code);
        }

        @Override
        public void decodeCoolDown(IClientHandler handler, ByteDecoder decoder) {
            int size = decoder.readInt();
            Map<CharSequence, Integer> map = new HashMap<>();
            for (int i = 0; i < size; i++){
                CharSequence key = decoder.readCharSequence();
                int cd = decoder.readInt();
                map.put(key,cd);
            }
            handler.onCoolDown(map);
        }
    }
}
