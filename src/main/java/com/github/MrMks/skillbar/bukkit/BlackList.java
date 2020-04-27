package com.github.MrMks.skillbar.bukkit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BlackList {
    private static BlackList instance;

    public static void init(File path){
        if (instance == null) instance = new BlackList(new File(path,"black_list.json"));
    }

    public static BlackList getInstance(){
        return instance;
    }

    public static void saveUnload(){
        if (instance != null) instance.writeToDisk();
        instance = null;
    }

    private final Set<String> strUidList = new HashSet<>();
    private final File file;
    public BlackList(File file){
        this.file = file;
        readFromDisk();
    }

    public void add(String uid){
        strUidList.add(uid);
    }

    public void remove(String uid){
        strUidList.remove(uid);
    }

    public boolean contain(String uid){
        return strUidList.contains(uid);
    }

    private void readFromDisk(){
        Gson gson = new Gson();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type type = new TypeToken<HashSet<String>>(){}.getType();
                strUidList.addAll(gson.fromJson(reader,type));
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void writeToDisk(){
        Gson gson = new Gson();
        ArrayList<String> list;
        synchronized (strUidList){
            list = new ArrayList<>(strUidList);
        }
        try (FileWriter writer = new FileWriter(file)){
            writer.write(gson.toJson(list));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
