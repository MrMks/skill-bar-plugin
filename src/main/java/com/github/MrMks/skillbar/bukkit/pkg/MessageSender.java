package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.common.ByteBuilder;
import com.github.MrMks.skillbar.common.Constants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class MessageSender {
    private final Plugin p;
    public MessageSender(Plugin plugin){
        this.p = plugin;
    }

    public void send(UUID uuid, byte i, ByteBuilder builder) {
        send(Bukkit.getPlayer(uuid), i, builder);
    }

    public void send(Player player, byte i , ByteBuilder builder) {
        byte[][] bytes = builder.build(i);
        for (byte[] b : bytes) {
            player.sendPluginMessage(p, Constants.CHANNEL_NAME, b);
        }
    }
}
