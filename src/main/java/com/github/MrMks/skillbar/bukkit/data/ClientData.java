package com.github.MrMks.skillbar.bukkit.data;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ClientData {
    private UUID uuid;
    private ClientStatus status;
    private ClientAccounts accounts;

    public ClientData(UUID uuid){
        this.uuid = uuid;
        this.status = new ClientStatus(uuid);
        this.accounts = new ClientAccounts(uuid);
    }

    public UUID getUniqueId(){
        return uuid;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public ClientAccounts getAccounts() {
        return accounts;
    }

    public boolean isValid(){
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        PlayerData playerData = SkillAPI.getPlayerData(player);
        return player != null
                && SkillAPI.isLoaded()
                && player.isOnline()
                && SkillAPI.getSettings().isWorldEnabled(player.getPlayer().getWorld())
                && SkillAPI.hasPlayerData(player)
                && playerData.getClasses().size() > 0
                && playerData.getSkills().size() > 0;
    }

    public void save(){
        accounts.saveToDisk();
    }

    public void clean(){
        uuid = null;
        status = null;
        accounts = null;
    }
}
