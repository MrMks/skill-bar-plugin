package com.github.MrMks.skillbar.common;

public class Constants {
    public static final int VERSION = 3;

    public static final String CHANNEL_NAME = "MCG_SkillBar";
    public static final byte DISCRIMINATOR = 0;

    public static final byte DISCOVER = 0;
    public static final byte SETTING = 1;
    public static final byte ENABLE = 2;
    public static final byte DISABLE = 3;

    public static final byte LIST_SKILL = 4;
    public static final byte ENFORCE_LIST_SKILL = 5;
    public static final byte UPDATE_SKILL = 6;
    public static final byte ENFORCE_UPDATE_SKILL = 7;
    public static final byte ADD_SKILL = 8;
    public static final byte LIST_BAR = 9;
    public static final byte SAVE_BAR = 10;

    public static final byte CAST = 11;
    public static final byte COOLDOWN = 12;
    public static final byte ACCOUNT = 13;
    public static final byte CLEAN = 14;

    public static final byte CAST_SUCCESS = 0;
    public static final byte CAST_FAILED_NO_SKILL = 1;
    public static final byte CAST_FAILED_UNLOCK = 2;
    public static final byte CAST_FAILED_COOLDOWN = 3;
    public static final byte CAST_FAILED_UNEXPECTED = 4;
}
