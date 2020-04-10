package com.github.MrMks.skillbar.common.handler;

import java.util.List;
import java.util.Map;

public interface IServerHandler {
    void onReceive();
    void onReceiveBad();

    void onDiscover();

    void onListSkill(List<String> keys);
    void onUpdateSkill(String key);
    void onListBar();
    void onSaveBar(Map<Integer, String> map);

    void onCast(String key);
}
