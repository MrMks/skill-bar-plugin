package com.github.MrMks.skillbar.bukkit;

import com.github.MrMks.skillbar.bukkit.condition.Condition;
import com.github.MrMks.skillbar.bukkit.data.*;
import com.github.MrMks.skillbar.bukkit.manager.ConditionManager;
import com.github.MrMks.skillbar.bukkit.task.ClientDiscoverTask;
import com.github.MrMks.skillbar.bukkit.task.ReloadCheckTask;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.*;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
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

import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings("unused")
public class MainListener implements Listener {

    private final Plugin plugin;
    private final ClientManager manager;
    private final ClientDiscoverTask task;
    public MainListener(Plugin plugin, ClientManager manager, ClientDiscoverTask cdt){
        this.plugin = plugin;
        this.manager = manager;
        this.task = cdt;
    }

    public void scheduler(Runnable runnable, long tick) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, tick);
    }

    /**
     * player join server, try to discover the client mod in the next short time;
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        manager.prepare(p);
        ClientData data = manager.get(p);
        if (data != null && !data.getStatus().isDiscovered()) {
            scheduler(()->{
                data.getEventHandler().onJoin();
                task.addName(data);
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
        ClientData data = manager.get(p);
        manager.unload(p.getUniqueId());
        if (data != null) task.removeName(data);
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
        ClientData data = manager.get(p);
        boolean c = checkClient(p);
        boolean v = checkValid(p);
        ArrayList<String> list = new ArrayList<>();
        for (PlayerClass playerClass : e.getPlayerData().getClasses()) list.add(playerClass.getData().getName());
        Optional<Condition> optional = ConditionManager.match(p.getWorld().getName(),list);
        if (c && v) {
            optional.ifPresent(condition -> data.getStatus().setCondition(condition.getKey()));
            data.getEventHandler().onChangeProfess();
            optional.ifPresent(condition -> data.getEventHandler().onMatchCondition(condition));
        } else if (v) {
            optional.ifPresent(condition -> data.getStatus().setCondition(condition.getKey()));
            data.getEventHandler().onStartProfess();
            optional.ifPresent(condition -> data.getEventHandler().onMatchCondition(condition));
        } else if (c) {
            data.getEventHandler().onUnMatchCondition();
            data.getEventHandler().onResetProfess();
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
        ClientData cData = manager.get(player);
        if (cData == null || !cData.getStatus().isDiscovered()) return;
        boolean p = !(e.getPreviousAccount() == null || e.getPreviousAccount().getClasses().isEmpty() || e.getPreviousAccount().getSkills().isEmpty());
        boolean n = !(e.getNewAccount() == null || e.getNewAccount().getClasses().isEmpty() || e.getNewAccount().getSkills().isEmpty());
        ArrayList<String> list = new ArrayList<>();
        if (n) e.getNewAccount().getClasses().forEach(v -> list.add(v.getData().getName()));
        Optional<Condition> optional = ConditionManager.match(player.getWorld().getName(), list);
        scheduler(()->{
            if (n) {
                if (p) {
                    optional.ifPresent(condition -> cData.getStatus().setCondition(condition.getKey()));
                    cData.getEventHandler().onAccountSwitch();
                    optional.ifPresent(condition -> cData.getEventHandler().onMatchCondition(condition));
                }
                else {
                    optional.ifPresent(condition -> cData.getStatus().setCondition(condition.getKey()));
                    cData.getEventHandler().onAccToEnable();
                    optional.ifPresent(condition -> cData.getEventHandler().onMatchCondition(condition));
                }
            } else {
                if (p) {
                    cData.getEventHandler().onUnMatchCondition();
                    cData.getEventHandler().onAccToDisable();
                }
            }
        },2);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e){
        //Player changed the world, the new world may now allowed to use Skill
        Player player = e.getPlayer();
        ClientData data = manager.get(player);
        ArrayList<String> list = new ArrayList<>();
        if (SkillAPI.hasPlayerData(player)){
            PlayerData playerData = SkillAPI.getPlayerData(player);
            playerData.getClasses().forEach(playerClass -> list.add(playerClass.getData().getName()));
        }
        Optional<Condition> optional = ConditionManager.match(player.getWorld().getName(),list);
        if (data != null && data.getStatus().isDiscovered()) {
            boolean f = SkillAPI.getSettings().isWorldEnabled(player.getWorld());
            if (f){
                if (!checkClient(data) && checkValid(player)) {
                    optional.ifPresent(condition -> data.getStatus().setCondition(condition.getKey()));
                    data.getEventHandler().onWorldToEnable();
                    optional.ifPresent(condition -> data.getEventHandler().onMatchCondition(condition));
                }
            } else {
                if (checkClient(data)) {
                    data.getEventHandler().onUnMatchCondition();
                    data.getEventHandler().onWorldToDisable();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSkillDowngrade(PlayerSkillDowngradeEvent e){
        if (e.isCancelled()) return;
        if (manager.has(e.getPlayerData().getUUID())) {
            ClientData clientData = manager.get(e.getPlayerData().getPlayer());
            if (!checkClient(clientData)) return;
            Bukkit.getScheduler().runTaskLater(plugin, () -> clientData.getEventHandler().onUpdateSkillInfo(e.getDowngradedSkill().getData().getKey()), 2);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSkillUpgrade(PlayerSkillUpgradeEvent e){
        if (e.isCancelled()) return;
        if (manager.has(e.getPlayerData().getPlayer())) {
            ClientData clientData = manager.get(e.getPlayerData().getPlayer());
            if (!checkClient(clientData)) return;
            Bukkit.getScheduler().runTaskLater(plugin,
                    ()-> clientData.getEventHandler().onUpdateSkillInfo(e.getUpgradedSkill().getData().getKey()),
                    2);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSkillUnlock(PlayerSkillUnlockEvent e){
        if (manager.has(e.getPlayerData().getUUID())) {
            ClientData clientData = manager.get(e.getPlayerData().getPlayer());
            if (!checkClient(clientData)) return;
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> clientData.getEventHandler().onUpdateSkillInfo(e.getUnlockedSkill().getData().getKey()),
                    2);
        }
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
        return manager.has(player) && checkClient(manager.get(player));
    }

    private boolean checkClient(ClientData m){
        return m != null && m.getStatus().isEnable();
    }
}