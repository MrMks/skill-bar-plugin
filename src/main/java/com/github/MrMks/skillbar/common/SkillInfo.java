package com.github.MrMks.skillbar.common;

import java.util.ArrayList;
import java.util.List;

public class SkillInfo {
    public static SkillInfo Empty = new SkillInfo("",false,false,false,0,(short)0,"",new ArrayList<>());
    String key;
    boolean exist;
    boolean isUnlock;
    boolean canCast;

    int itemId;
    short damage;
    String display;
    List<? extends CharSequence> lore;

    public SkillInfo(CharSequence key, boolean exist, boolean isUnlock, boolean canCast, int itemId, short damage, CharSequence display, List<? extends CharSequence> lore){
        this.key = (key == null ? "" : key.toString());
        this.exist = exist;
        this.isUnlock = isUnlock;
        this.canCast = canCast;
        this.itemId = itemId;
        this.damage = damage;
        this.display = (display == null ? "" : display.toString());
        this.lore = lore;
    }

    public String getKey() {
        return key;
    }

    public boolean isExist() {
        return exist;
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

    public List<? extends CharSequence> getLore() {
        return lore;
    }
}
