package com.github.MrMks.skillbar.bukkit.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class PlayerBar {
    private static File f;
    public static void setPath(File folder){
        f = folder;
    }

    private UUID uuid;
    private HashMap<Integer, Map<Integer, String>> map;
    private File file;
    PlayerBar(UUID uid){
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
        PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
        for (Map<Integer, String> sMap : map.values()){
            ArrayList<Integer> list = new ArrayList<>(9);
            for (Map.Entry<Integer, String> entry : sMap.entrySet()){
                if (data != null && !data.hasSkill(entry.getValue())) list.add(entry.getKey());
            }
            for (Integer k : list) sMap.remove(k);
        }
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
