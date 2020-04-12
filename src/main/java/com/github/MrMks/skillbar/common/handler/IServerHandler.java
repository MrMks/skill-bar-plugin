package com.github.MrMks.skillbar.common.handler;

import java.util.List;
import java.util.Map;

public interface IServerHandler {
    void onDiscover();

    void onListSkill(List<CharSequence> keys);
    void onUpdateSkill(CharSequence key);
    void onListBar();
    void onSaveBar(Map<Integer, CharSequence> map);

    void onCast(CharSequence key);
}
