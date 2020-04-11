package com.github.MrMks.skillbar.common;

import java.util.List;

public class SkillInfo {
    String key;
    boolean isUnlock;
    boolean canCast;

    int itemId;
    short damage;
    String display;
    List<CharSequence> lore;

    public SkillInfo(String key, boolean isUnlock, boolean canCast, int itemId, short damage, String display, List<CharSequence> lore){
        this.key = key;
        this.isUnlock = isUnlock;
        this.canCast = canCast;
        this.itemId = itemId;
        this.damage = damage;
        this.display = display;
        this.lore = lore;
    }

    public String getKey() {
        return key;
    }

    public boolean isUnlock() {
        return isUnlock;
    }

    public boolean canCast() {
        return canCast;
    }

    public int getItemId() {
        return itemId;
    }

    public short getDamage() {
        return damage;
    }

    public String getDisplay() {
        return display;
    }

    public List<CharSequence> getLore() {
        return lore;
    }
}
