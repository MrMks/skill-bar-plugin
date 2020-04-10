package com.github.MrMks.skillbar.data;

import com.github.MrMks.skillbar.Setting;
import com.google.gson.Gson;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PlayerBar {
    private static File f;
    public static void setPath(File folder){
        f = folder;
    }

    private UUID uuid;
    private File file;
    private Map<Integer, Map<Integer, String>> map;
    // global setting
    private int maxLine;
    // setting related to activeAcc
    private int nowLine = 0;
    PlayerBar(UUID uid){
        uuid = uid;
        file = new File(f,"player/" + uuid.toString() + ".json");
        maxLine = Setting.getInstance().getBarMaxLine();
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

    public int max(){
        return maxLine;
    }

    //this two method may not be used in short time;
    public int getNowLine(){
        return nowLine;
    }

    public void setNowLine(int nLine){
        nowLine = Math.min(nLine, maxLine);
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
        if (map == null) return;
        ArrayList<Integer> list = new ArrayList<>();
        for (Map.Entry<Integer,String> entry : map.entrySet()){
            if (entry.getKey() >= (maxLine + 1) * 9) list.add(entry.getKey());
        }
        for (Integer key : list) map.remove(key);
        this.map.put(activeId, map);
    }

    public void readFromFile(){
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                SaveFile saveFile = gson.fromJson(reader, SaveFile.class);
                nowLine = saveFile.nowLine;
                map = saveFile.map;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (map == null) map = new HashMap<>();
        PlayerData data = SkillAPI.getPlayerData(Bukkit.getOfflinePlayer(uuid));
        for (Map<Integer, String> sMap : map.values()){
            ArrayList<Integer> list = new ArrayList<>();
            for (Map.Entry<Integer, String> entry : sMap.entrySet()){
                if (data != null && !data.hasSkill(entry.getValue())) list.add(entry.getKey());
                if (entry.getKey() >= (maxLine + 1) * 9) list.add(entry.getKey());
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
            gson.toJson(new SaveFile(Math.min(nowLine, maxLine), map),writer);
            writer.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static class SaveFile {
        // save file format version, may have no need to use
        // this means delete previous format file after every update
        //private int version = 3;

        // considering do not save this value; return to 0 every time the client join;
        private int nowLine;
        private Map<Integer, Map<Integer, String>> map;
        SaveFile(int nowLine, Map<Integer, Map<Integer, String>> map){
            this.nowLine = nowLine;
            this.map = map;
        }
    }
}