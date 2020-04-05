package com.github.MrMks.skillbar.bukkit.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sucy.skill.SkillAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PlayerBar {
    private static HashMap<UUID, PlayerBar> barMap = new HashMap<>();
    public static PlayerBar get(Player player){
        if (!barMap.containsKey(player.getUniqueId())){
            barMap.put(player.getUniqueId(), new PlayerBar(player.getUniqueId()));
        }
        return barMap.get(player.getUniqueId());
    }

    static PlayerBar get(UUID uid){
        if (!barMap.containsKey(uid)){
            barMap.put(uid, new PlayerBar(uid));
        }
        return barMap.get(uid);
    }

    public static void unloadSave(Player player){
        PlayerBar bar = barMap.remove(player.getUniqueId());
        if (bar != null) bar.saveToFile();
    }

    static void unloadSave(UUID uid){
        PlayerBar bar = barMap.remove(uid);
        if (bar != null) bar.saveToFile();
    }

    public static void unloadSaveAll(){
        for (UUID key : barMap.keySet()){
            barMap.get(key).saveToFile();
        }
        barMap.clear();
    }

    private static File f;
    public static void setPath(File folder){
        f = folder;
    }

    private UUID uuid;
    private HashMap<Integer, Map<Integer, String>> map;
    private File file;
    private PlayerBar(UUID uid){
        uuid = uid;
        file = new File(f,"player/" + uuid.toString() + ".json");
        readFromFile();
    }

    private int getActiveId(){
        return SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
    }

    public boolean isEmpty(){
        int id = getActiveId();
        return map == null || map.get(id) == null || map.get(id).isEmpty();
    }

    public int size(){
        int id = getActiveId();
        return (map == null || map.get(id) == null) ? 0 : map.get(id).size();
    }

    public Set<Integer> keys(){
        int id = getActiveId();
        return (map == null || map.get(id) == null) ? Collections.emptySet() : map.get(id).keySet();
    }

    public String getSkill(Integer order){
        int id = getActiveId();
        return (map == null || map.get(id) == null) ? "" : map.get(id).get(order);
    }


    public void setBar(int activeId, Map<Integer, String> map) {
        if (this.map == null) this.map = new HashMap<>();
        this.map.put(activeId, map);
    }

    public void readFromFile(){
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<Integer, HashMap<Integer, String>>>() {
                }.getType();
                map = gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (map == null) map = new HashMap<>();
    }

    public void saveToFile(){
        if (file.exists()) file.delete();
        if (map.isEmpty()) return;
        boolean flag = false;
        for (Map<Integer, String> m : map.values()){
            flag = flag || m.isEmpty();
        }
        if (flag) return;
        try (FileWriter writer = new FileWriter(file)){
            Gson gson = new Gson();
            gson.toJson(map,writer);
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
