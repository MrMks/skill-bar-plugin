package com.github.MrMks.skillbar.pkg;

import com.github.MrMks.skillbar.common.SkillInfo;
import com.sucy.skill.api.player.PlayerSkill;
import com.sucy.skill.api.skills.Skill;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

public class BukkitSkillInfo extends SkillInfo {
    private static int itemMethodFlag = 0;
    private static ItemStack getItemStack(PlayerSkill skill){
        if (itemMethodFlag == 0) {
            try {
                Skill.class.getMethod("getIndicator", PlayerSkill.class, boolean.class);
                itemMethodFlag += 1;
            }catch (NoSuchMethodException ignored){}
            try {
                //noinspection JavaReflectionMemberAccess
                Skill.class.getMethod("getIndicator", PlayerSkill.class);
                itemMethodFlag += 2;
            }catch (NoSuchMethodException ignored){}
        }
        ItemStack stack = null;
        switch (itemMethodFlag){
            case 1:
                try {
                    Method method = Skill.class.getMethod("getIndicator", PlayerSkill.class, boolean.class);
                    stack = (ItemStack) method.invoke(skill.getData(),skill, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
                break;
            case 2:
                try {
                    // method used in non-premium skillapi
                    @SuppressWarnings("JavaReflectionMemberAccess")
                    Method method = Skill.class.getMethod("getIndicator", PlayerSkill.class);
                    stack = (ItemStack) method.invoke(skill.getData(), skill);
                } catch (Exception ignored){}
                break;
            default:
                stack = new ItemStack(Material.AIR);
                break;
        }
        return stack;
    }

    public BukkitSkillInfo(String key, boolean isUnlock, boolean canCast, ItemStack stack) {
        super(key,true, isUnlock, canCast, stack.getTypeId(),
                stack.getDurability(),
                stack.hasItemMeta() ? stack.getItemMeta().getDisplayName() : "",
                stack.hasItemMeta() ? stack.getItemMeta().getLore() : new ArrayList<>(0));
    }
    public BukkitSkillInfo(PlayerSkill skill){
        this(skill.getData().getKey(), skill.isUnlocked(), skill.getData().canCast(), getItemStack(skill));
    }
    public BukkitSkillInfo(String key){
        super(key,false,false,false,0,(short) 0,"", Collections.emptyList());
    }
}
