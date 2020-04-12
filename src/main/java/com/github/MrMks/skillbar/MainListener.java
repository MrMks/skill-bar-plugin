package com.github.MrMks.skillbar;

import com.github.MrMks.skillbar.data.*;
import com.github.MrMks.skillbar.pkg.PackageSender;
import com.github.MrMks.skillbar.task.ClientDiscoverTask;
import com.github.MrMks.skillbar.task.ReloadCheckTask;
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
    private ClientManager manager;
    private ClientDiscoverTask task;
    public MainListener(Plugin plugin, PackageSender sender, ClientManager manager, ClientDiscoverTask cdt){
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
        ClientStatus m = manager.get(p);
        if (m != null && !m.isDiscovered()) {
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
        ClientStatus m = manager.get(p);
        manager.unload(p.getUniqueId());
        if (m != null) task.removeName(m);
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
        boolean c = checkClient(p);
        boolean v = checkValid(p);
        if (c && v) {
            sender.sendAddSkill(p);
        } else if (v) {
            sender.sendEnable(p);
        } else if (c) {
            sender.sendClearClientList(p);
            sender.sendDisable(p);
            manager.get(p).getBar().setBar(SkillAPI.getPlayerAccountData(p).getActiveId(), Collections.emptyMap());
        }
    }

    /**
     * this handler will be called before account change, so it requires a delay to run;
     * if event is cancelled, this handler will do nothing;
     * @param e event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeAccount(PlayerAccountChangeEvent e){
        if (e.isCancelled()) return;
        Player player = e.getAccountData().getPlayer();
        ClientStatus cData = manager.get(player);
        if (cData == null || !cData.isDiscovered()) return;
        boolean p = !(e.getPreviousAccount() == null || e.getPreviousAccount().getClasses().isEmpty() || e.getPreviousAccount().getSkills().isEmpty());
        boolean n = !(e.getNewAccount() == null || e.getNewAccount().getClasses().isEmpty() || e.getNewAccount().getSkills().isEmpty());
        if (n) {
            if (p) Bukkit.getScheduler().runTaskLater(plugin, ()->sender.sendAccount(player),2);
            else Bukkit.getScheduler().runTaskLater(plugin, ()->sender.sendEnable(player),2);
        } else {
            if (p) Bukkit.getScheduler().runTaskLater(plugin, ()->sender.sendDisable(player),2);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e){
        //Player changed the world, the new world may now allowed to use Skill
        Player player = e.getPlayer();
        ClientStatus m = manager.get(player);
        if (m != null && m.isDiscovered()) {
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
        ClientStatus clientData = manager.get(e.getPlayerData().getPlayer());
        if (!checkClient(clientData)) return;
        Bukkit.getScheduler().runTaskLater(plugin, ()-> sender.sendEnforceUpdateSkill(e.getPlayerData().getPlayer(), e.getDowngradedSkill().getData().getKey()),2);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSkillUpgrade(PlayerSkillUpgradeEvent e){
        if (e.isCancelled()) return;
        ClientStatus clientData = manager.get(e.getPlayerData().getPlayer());
        if (!checkClient(clientData)) return;
        Bukkit.getScheduler().runTaskLater(plugin,
                ()-> sender.sendEnforceUpdateSkill(e.getPlayerData().getPlayer(), e.getUpgradedSkill().getData().getKey()),
                2);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSkillUnlock(PlayerSkillUnlockEvent e){
        ClientStatus clientData = manager.get(e.getPlayerData().getPlayer());
        if (!checkClient(clientData)) return;
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
        return player != null
                && SkillAPI.isLoaded()
                && SkillAPI.getSettings().isWorldEnabled(player.getWorld())
                && SkillAPI.hasPlayerData(player)
                && !SkillAPI.getPlayerData(player).getClasses().isEmpty()
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

    private boolean checkClient(ClientStatus m){
        return m != null && m.getStatus() == EnumStatus.Enabled;
    }
}