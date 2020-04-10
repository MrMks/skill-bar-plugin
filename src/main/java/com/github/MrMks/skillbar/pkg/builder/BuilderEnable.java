package com.github.MrMks.skillbar.pkg.builder;

import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.pkg.IServerBuilder;
import com.github.MrMks.skillbar.pkg.BukkitByteBuilder;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class BuilderEnable implements IServerBuilder {
    @Override
    public ByteBuilder build(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        PlayerAccounts accounts = SkillAPI.getPlayerAccountData(player);
        PlayerData data = SkillAPI.getPlayerData(player);

        return new BukkitByteBuilder(Constants.ENABLE)
                .writeInt(accounts.getActiveId()) // active Id
                .writeInt(data.getSkills().size()); // skill size
    }

    @Override
    public boolean isBuildable(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return SkillAPI.isLoaded() && SkillAPI.hasPlayerData(player);
    }
}
