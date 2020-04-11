package com.github.MrMks.skillbar.data;

import com.github.MrMks.skillbar.common.handler.IServerHandler;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * this class will handle package receive and send;
 * almost every method written in {@link com.github.MrMks.skillbar.pkg.PackageSender} and {@link com.github.MrMks.skillbar.pkg.PackageListener} will be re-write in this class;
 * and this will contain {@link ClientData};
 */
public class PlayerHandler implements IServerHandler {
    private static Map<UUID, PlayerHandler> map = new HashMap<>();
    public static PlayerHandler getHandler(Player player){
        return player == null ? null : getHandler(player.getUniqueId());
    }

    public static PlayerHandler getHandler(UUID uuid){
        if (!map.containsKey(uuid)) map.put(uuid, new PlayerHandler(uuid));
        return map.get(uuid);
    }

    public static void clear(){
        for (PlayerHandler handler : map.values()) {
            handler.clean();
        }
        map.clear();
    }

    private ClientData data;
    private PlayerBar bar;
    private PlayerHandler(UUID uuid){
        this.data = new ClientData(uuid);
        this.bar = new PlayerBar(uuid);
    }

    @Override
    public void onDiscover() {
        data.discover();
    }

    @Override
    public void onListSkill(List<String> keys) {

    }

    @Override
    public void onUpdateSkill(String key) {

    }

    @Override
    public void onListBar() {

    }

    @Override
    public void onSaveBar(Map<Integer, String> map) {

    }

    @Override
    public void onCast(String key) {

    }

    public ClientData getClientData(){
        return data;
    }

    public PlayerBar getBar(){
        return bar;
    }

    public void clean(){

    }

}
