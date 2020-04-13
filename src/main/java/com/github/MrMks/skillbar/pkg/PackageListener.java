package com.github.MrMks.skillbar.pkg;

import com.github.MrMks.skillbar.common.ByteDecoder;
import com.github.MrMks.skillbar.common.Constants;
import com.github.MrMks.skillbar.common.handler.IServerHandler;
import com.github.MrMks.skillbar.common.pkg.CPackage;
import com.github.MrMks.skillbar.data.ClientData;
import com.github.MrMks.skillbar.data.ClientManager;
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
            if ((data.getStatus().isBlocked() && data.getStatus().isSendDisable()) || data.getStatus().isDisable()) {
                data.getEventHandler().disable();
                return;
            }
            try {
                ByteDecoder decoder = new ByteDecoder(bytes);
                IServerHandler handler = data.getPackageHandler();
                switch (decoder.getHeader()) {
                    case Constants.DISCOVER:
                        CPackage.DECODER.decodeDiscover(handler, decoder);
                        break;
                    case Constants.LIST_SKILL:
                        CPackage.DECODER.decodeListSkill(handler, decoder);
                        break;
                    case Constants.UPDATE_SKILL:
                        CPackage.DECODER.decodeUpdateSkill(handler, decoder);
                        break;
                    case Constants.CAST:
                        CPackage.DECODER.decodeCast(handler, decoder);
                        break;
                    case Constants.LIST_BAR:
                        CPackage.DECODER.decodeListBar(handler, decoder);
                        break;
                    case Constants.SAVE_BAR:
                        CPackage.DECODER.decodeSaveBar(handler, decoder);
                        break;
                    default:
                        Bukkit.getLogger().warning("Undefined package header received from player: " + player.getName());
                        break;
                }
                data.getStatus().onReceive();
            } catch (Throwable tr) {
                tr.printStackTrace();
                data.getStatus().onReceiveBad();
            }
        }
    }
}