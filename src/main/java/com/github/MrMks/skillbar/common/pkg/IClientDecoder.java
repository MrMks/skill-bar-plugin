package com.github.MrMks.skillbar.common.pkg;

import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.handler.IClientHandler;

public interface IClientDecoder {
    void decode(IClientHandler handler, ByteDecoder decoder);
}