package com.github.MrMks.skillbar.common;

import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.handler.IServerHandler;

public interface IDecoderCP {
    void decode(IServerHandler handler, ByteDecoder decoder);
}
