package com.github.MrMks.skillbar.bukkit.data;

import com.github.MrMks.skillbar.bukkit.Setting;
import com.google.gson.Gson;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ClientBar {
    private static File f;
    public static void setPath(File folder){
        f = folder;
    }

    private UUID uuid;
    private File file;
    private AccountBar accountBar = new AccountBar();
    private ConditionBar conditionBar = new ConditionBar();
    // private Map<Integer, Map<Integer, String>> map;
    // private Map<String, Map<Integer, String>> conditionMap;
    // global setting
    // private int maxLine;

    public ClientBar(UUID uid){
        uuid = uid;
        file = new File(f,"player/" + uuid.toString() + ".json");
        //maxLine = Setting.getInstance().getBarMaxLine();
        readFromFile();
    }

    private int getActiveId(){
        return SkillAPI.getPlayerAccountData(Bukkit.getOfflinePlayer(uuid)).getActiveId();
    }

    public Map<Integer, String> getAccountBar(){
        return accountBar.getMap(getActiveId());
    }

    public Set<Integer> getAccountBarKeys(){
        int id  = getActiveId();
        return accountBar.getMap(id).keySet();
    }

    public String getAccountBarSkill(Integer order){
        int id = getActiveId();
        return accountBar.getMap(order).getOrDefault(order,"");
    }

    public boolean hasConditionBar(String key) {
        return conditionBar.map.containsKey(key);
    }

    public Map<Integer, String> getConditionBar(String key){
        return conditionBar.getMap(key);
    }

    public Set<Integer> getConditionBarKeys(String key){
        return conditionBar.getMap(key).keySet();
    }

    public String getConditionBarSkill(String key, int order){
        return conditionBar.getMap(key).getOrDefault(order, "");
    }

    public void setAccountBar(Map<Integer, String> map){
        setAccountBar(getActiveId(), map);
    }

    public void setAccountBar(int activeId, Map<Integer, String> map) {
        if (this.accountBar.map == null) this.accountBar.map = new HashMap<>();
        if (map == null) return;
        this.accountBar.setMap(activeId, map);
    }

    public void setConditionBar(String key, Map<Integer, String> map){
        if (this.conditionBar.map == null) this.conditionBar.map = new HashMap<>();
        if (key == null || map == null) return;
        conditionBar.setMap(key, map);
    }

    public void readFromFile(){
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Save saveFile = gson.fromJson(reader, Save.class);
                accountBar.map = saveFile.map;
                conditionBar.map = saveFile.conditionMap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (accountBar.map == null) accountBar.map = new HashMap<>();
        if (conditionBar.map == null) conditionBar.map = new HashMap<>();
        PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
        for (Map<Integer, String> sMap : conditionBar.map.values()){
            ArrayList<Integer> list = new ArrayList<>();
            for (Map.Entry<Integer, String> entry : sMap.entrySet()){
                if (data != null && !data.hasSkill(entry.getValue())) list.add(entry.getKey());
                if (entry.getKey() >= (Setting.getInstance().getBarMaxLine() + 1) * 9) list.add(entry.getKey());
            }
            for (Integer k : list) sMap.remove(k);
        }
    }

    public void saveToFile(){
        boolean del = !file.exists();
        if (!del) del = file.delete();
        if (del) {
            if (accountBar.map.isEmpty() && conditionBar.map.isEmpty()) return;
            try (FileWriter writer = new FileWriter(file)) {
                Gson gson = new Gson();
                gson.toJson(new Save(accountBar.map, conditionBar.map), writer);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Save {
        // save file format version, may have no need to use
        // this means delete previous format file after every update
        //private int version = 3;

        // considering do not save this value; return to 0 every time the client join;
        private final Map<Integer, Map<Integer, String>> map;
        private final Map<String, Map<Integer, String>> conditionMap;
        Save(Map<Integer, Map<Integer, String>> map, Map<String, Map<Integer, String>> conditionMap){
            this.map = map;
            this.conditionMap = conditionMap;
        }
    }

    private static class AccountBar {
        private Map<Integer, Map<Integer, String>> map;
        private AccountBar(){}
        void setMap(Integer active, Map<Integer, String> map) {
            if (map == null) return;
            map.keySet().removeIf(v -> v >= 0 && v < Setting.getInstance().getBarMaxLine());
            this.map.put(active, map);
        }

        Map<Integer,String> getMap(Integer active){
            return new HashMap<>(map.getOrDefault(active, Collections.emptyMap()));
        }
    }

    public static class ConditionBar {
        private Map<String, Map<Integer, String>> map;
        private ConditionBar(){}
        void setMap(String key, Map<Integer, String> map) {
            if (map == null) return;
            this.map.put(key, map);
        }

        Map<Integer,String> getMap(String key){
            return new HashMap<>(map.getOrDefault(key, Collections.emptyMap()));
        }

    }
}