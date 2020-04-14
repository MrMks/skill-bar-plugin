package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class PluginSender {
    private static Plugin p;
    public static void init(Plugin plugin){
        p = plugin;
    }

    public static void clean(){
        p = null;
    }

    private byte index = 0;
    private UUID uuid;
    public PluginSender(UUID uuid){
        this.uuid = uuid;
    }

    public void send(ByteBuilder builder) {
        if (index == Byte.MAX_VALUE) index = 0;
        byte i = index++;
        for (byte[] b : builder.build(i)) {
            Bukkit.getPlayer(uuid).sendPluginMessage(p, Constants.CHANNEL_NAME, b);
        }
    }
}
