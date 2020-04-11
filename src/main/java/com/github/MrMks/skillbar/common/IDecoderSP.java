package com.github.MrMks.skillbar.common;

import com.github.MrMks.skillbar.common.handler.IClientHandler;

public interface IDecoderSP {
    void decodeDiscover(IClientHandler handler, ByteDecoder decoder);
    void decodeSetting(IClientHandler handler, ByteDecoder decoder);
    void decodeEnable(IClientHandler handler, ByteDecoder decoder);
    void decodeAccount(IClientHandler handler, ByteDecoder decoder);
    void decodeCleanUp(IClientHandler handler, ByteDecoder decoder);
    void decodeDisable(IClientHandler handler, ByteDecoder decoder);

    void decodeListSkill(IClientHandler handler, ByteDecoder decoder);
    void decodeEnforceListSkill(IClientHandler handler, ByteDecoder decoder);
    void decodeUpdateSkill(IClientHandler handler, ByteDecoder decoder);
    void decodeEnforceUpdateSkill(IClientHandler handler, ByteDecoder decoder);
    void decodeAddSkill(IClientHandler handler, ByteDecoder decoder);
    void decodeListBar(IClientHandler handler, ByteDecoder decoder);

    void decodeCast(IClientHandler handler, ByteDecoder decoder);
    void decodeCoolDown(IClientHandler handler, ByteDecoder decoder);
}