package com.github.MrMks.skillbar.bukkit.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientAccounts {
    private static File f;
    public static void setPath(File file){
        f = file;
    }

    private final Map<Integer, ClientAccount> map = new HashMap<>();
    private final File file;
    public ClientAccounts(UUID uuid){
        this.file = new File(f, "player/" + uuid.toString() + ".json");
        readFromDisk();
    }

    public ClientAccount getAccount(int active){
        if (!map.containsKey(active)) map.put(active, new ClientAccount());
        return map.get(active);
    }

    public void readFromDisk(){
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<Integer, ClientAccount.Save>>(){}.getType();
        HashMap<Integer, ClientAccount.Save> map = null;
        try (FileReader reader = new FileReader(file)) {
            map = gson.fromJson(reader, type);
        } catch (Throwable e){
            map = new HashMap<>();
        } finally {
            if (map == null) map = new HashMap<>();
            for (Map.Entry<Integer, ClientAccount.Save> entry : map.entrySet()) {
                ClientAccount account = getAccount(entry.getKey());
                account.setSave(entry.getValue());
            }
        }
    }

    public void saveToDisk(){
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<Integer, ClientAccount.Save>>(){}.getType();
        HashMap<Integer, ClientAccount.Save> map = new HashMap<>();
        for (Map.Entry<Integer, ClientAccount> entry : this.map.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getSave());
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(gson.toJson(map,type));
        } catch (IOException ignored){}
    }
}
