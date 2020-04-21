package com.github.MrMks.skillbar.bukkit.pkg;

import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.handler.IServerHandler;
import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.bukkit.data.ClientData;
import com.github.MrMks.skillbar.bukkit.manager.ClientManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PackageListener implements PluginMessageListener {
    private final ClientManager manager;
    public PackageListener(ClientManager manager){
        this.manager = manager;
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (!s.equals(Constants.CHANNEL_NAME) || player == null) return;
        if (manager.has(player)) {
            ClientData data = manager.get(player);
            if ((data.getStatus().isBlocked() && data.getStatus().canDisableOnBlocked()) || data.getStatus().isDisabled()) {
                data.getEventHandler().sendDisable();
                return;
            }
            try {
                ByteDecoder decoder = new ByteDecoder(bytes);
                IServerHandler handler = data.getPackageHandler();
                switch (decoder.getHeader()) {
                    case Discover:
                        CPackage.DECODER.decodeDiscover(handler, decoder);
                        break;
                    case ListSkill:
                        CPackage.DECODER.decodeListSkill(handler, decoder);
                        break;
                    case UpdateSkill:
                        CPackage.DECODER.decodeUpdateSkill(handler, decoder);
                        break;
                    case Cast:
                        CPackage.DECODER.decodeCast(handler, decoder);
                        break;
                    case ListBar:
                        CPackage.DECODER.decodeListBar(handler, decoder);
                        break;
                    case SaveBar:
                        CPackage.DECODER.decodeSaveBar(handler, decoder);
                        break;
                    default:
                        Bukkit.getLogger().warning("Undefined package header received from player: " + player.getName());
                        break;
                }
                data.getStatus().receive();
            } catch (Throwable tr) {
                tr.printStackTrace();
                data.getStatus().receiveBad();
            }
        }
    }
}