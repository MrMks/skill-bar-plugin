package com.github.MrMks.skillbar.pkg;

import com.github.MrMks.skillbar.common.SkillInfo;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BukkitSkillInfo extends SkillInfo {
    public BukkitSkillInfo(String key, boolean isUnlock, boolean canCast, ItemStack stack) {
        super(key, isUnlock, canCast, stack.getTypeId(),
                stack.getDurability(),
                stack.hasItemMeta() ? stack.getItemMeta().getDisplayName() : "",
                stack.hasItemMeta() ? stack.getItemMeta().getLore() : new ArrayList<>(0));
    }
}
