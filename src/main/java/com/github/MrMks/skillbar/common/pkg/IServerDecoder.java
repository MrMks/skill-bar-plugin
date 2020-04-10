package com.github.MrMks.skillbar.common.pkg;

import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.handler.IServerHandler;

public interface IServerDecoder {
    void decode(IServerHandler handler, ByteDecoder decoder);
}
