package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.manager.*;
import com.github.MrMks.skillbar.bukkit.pkg.PackageSender;
import com.github.MrMks.skillbar.bukkit.task.ClientDiscoverTask;
import com.github.MrMks.skillbar.bukkit.task.ReloadCheckTask;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

@SuppressWarnings("unused")
public class MainListener implements Listener {

    private Plugin plugin;
    private PackageSender sender;
    private Manager manager;
    private ClientDiscoverTask task;
    public MainListener(Plugin plugin, PackageSender sender, Manager manager, ClientDiscoverTask cdt){
        this.plugin = plugin;
        this.sender = sender;
        this.manager = manager;
        this.task = cdt;
    }

    /**
     * player join server, try to enable skillBar if the world is allowed to use skill;
     * whether or not allowed to enable will be checked in SkillBarSender
     * this send is early than player join server
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        ClientData m = manager.get(p);
        if (!m.isDiscovered()) {
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                sender.sendDiscover(p);
                task.addName(m);
            }, 20);
        }
    }

    /**
     * player disconnect from server event will fire on client, so client will handle that event to clean local caches
     * this event will only use to clean server files;
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExit(PlayerQuitEvent e){
        //Player exit server, clear instances;
        Player p = e.getPlayer();
        ClientData m = manager.get(p);
        if (checkClient(m)){
            // Client will disable when client handle disconnect event
            // PackageSender.sendDisable(e.getPlayer());
            PlayerBar.unloadSave(p);
            task.removeName(m);
            m.unload();
        }
    }

    /**
     * player change class, including using cmd @code{/class reset confirm};
     * previous can be null if the new class is the first class in it's group;
     *
     * client status will consider as previous status of valid check, as if previous valid check is passed, the client check now should pass;
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeClass(PlayerClassChangeEvent e){
        Player p = e.getPlayerData().getPlayer();
        if (checkClient(p) && checkValid(p)) {
            sender.sendAddSkill(p);
        } else if (checkValid(p)) {
            sender.sendEnable(p);
        } else if (checkClient(p)) {
            sender.sendClearClientList(p);
            sender.sendDisable(p);
        }
        PlayerBar.get(p).setBar(SkillAPI.getPlayerAccountData(p).getActiveId(), Collections.emptyMap());
    }

    /**
     * this handler will be called before account change, so it requires a delay to run;
     * if event is cancelled, this handler will do nothing;
     * @param e event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeAccount(PlayerAccountChangeEvent e){
        if (e.isCancelled()) return;
        if (!manager.get(e.getAccountData().getPlayer()).isDiscovered()) return;
        boolean p = !(e.getPreviousAccount() == null || e.getPreviousAccount().getSkills().size() == 0);
        boolean n = !(e.getNewAccount() == null || e.getNewAccount().getSkills().size() == 0);
        Player player = e.getAccountData().getPlayer();
        if (p) {
            Bukkit.getScheduler().runTaskLater(plugin, ()->sender.sendAccount(player),2);
        } else {
            if (n) Bukkit.getScheduler().runTaskLater(plugin, ()->sender.sendEnable(player),2);
            else Bukkit.getScheduler().runTaskLater(plugin, ()->sender.sendDisable(player),2);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e){
        //Player changed the world, the new world may now allowed to use Skill
        Player player = e.getPlayer();
        ClientData m = manager.get(player);
        if (m.isDiscovered()) {
            boolean f = SkillAPI.getSettings().isWorldEnabled(player.getWorld());
            if (f){
                if (!checkClient(m) && checkValid(player)) sender.sendEnable(e.getPlayer());
            } else {
                if (checkClient(m)) sender.sendDisable(e.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSkillDowngrade(PlayerSkillDowngradeEvent e){
        if (e.isCancelled()) return;
        if (!manager.get(e.getPlayerData().getPlayer()).isDiscovered()) return;
        Bukkit.getScheduler().runTaskLater(plugin, ()-> sender.sendEnforceUpdateSkill(e.getPlayerData().getPlayer(), e.getDowngradedSkill().getData().getKey()),2);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSkillUpgrade(PlayerSkillUpgradeEvent e){
        if (e.isCancelled()) return;
        if (!manager.get(e.getPlayerData().getPlayer()).isDiscovered()) return;
        Bukkit.getScheduler().runTaskLater(plugin,
                ()-> sender.sendEnforceUpdateSkill(e.getPlayerData().getPlayer(), e.getUpgradedSkill().getData().getKey()),
                2);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSkillUnlock(PlayerSkillUnlockEvent e){
        if (!manager.get(e.getPlayerData().getPlayer()).isDiscovered()) return;
        Bukkit.getScheduler().runTaskLater(plugin,
                ()-> sender.sendEnforceUpdateSkill(e.getPlayerData().getPlayer(), e.getUnlockedSkill().getData().getKey()),
                2);
    }

    /**
     * read command and check permission;
     * if passed then this plugin will be disable;
     * then enable when the SkillAPI.isLoad() is true after 1s;
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent e){
        if (e.isCancelled()) return;
        if (e.getMessage().trim().replace("/","").equals("class reload")){
            if (e.getPlayer().hasPermission("skillapi.reload")){
                plugin.onDisable();
                Bukkit.getScheduler().runTaskLater(plugin,new ReloadCheckTask(plugin, e.getPlayer()), 20);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerCommand(ServerCommandEvent e){
        if (e.isCancelled()) return;
        if (e.getCommand().trim().replace("/","").equals("class reload")){
            plugin.onDisable();
            Bukkit.getScheduler().runTaskLater(plugin,new ReloadCheckTask(plugin, e.getSender()), 20);
        }
    }

    private boolean checkValid(Player player){
        return SkillAPI.isLoaded()
                && SkillAPI.getSettings().isWorldEnabled(player.getWorld())
                && SkillAPI.hasPlayerData(player)
                && !SkillAPI.getPlayerData(player).getSkills().isEmpty();
    }

    /**
     *
     * @param player the player to check
     * @return true if player status is Enabled
     */
    private boolean checkClient(Player player){
        return checkClient(manager.get(player));
    }

    private boolean checkClient(ClientData m){
        return m != null && m.getStatus() == ClientStatus.Enabled;
    }
}
