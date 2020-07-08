package com.github.MrMks.skillbar.bukkit.modules;

import java.io.File;
import java.util.HashMap;

public class ModuleManager {
    private static HashMap<String, Module> moduleHashMap;
    private static final Module disabled = new DisabledModule();
    public static Module getModule(String moduleName){
        return moduleHashMap.getOrDefault(moduleName, disabled);
    }

    private static File path;
    public static void setPath(File file){
        path = new File(file, "modules");
    }
}
