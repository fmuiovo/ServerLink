package com.serverlink.util;

import com.serverlink.Main;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class TransferUtil {

    public static boolean supportPaperTransfer() {
        try {
            Class.forName("io.papermc.paper.entity.TeleportFlag");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void paperTransfer(Player player, String host, int port) {
        InetSocketAddress socketAddr = new InetSocketAddress(host, port);
        player.performCommand("transfer " + host + " " + port);
    }

    public static boolean proxyTransfer(Player player, String proxyServerName) {
        Plugin plugin = Main.getInstance();
        if (!player.getServer().getMessenger().isOutgoingChannelRegistered(plugin, "BungeeCord")) {
            plugin.getLogger().warning("BungeeCord传出通道未注册！");
            return false;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            dos.writeUTF("Connect");
            dos.writeUTF(proxyServerName);

            player.sendPluginMessage(plugin, "BungeeCord", baos.toByteArray());
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("Proxy transfer error: " + e.getMessage());
            return false;
        }
    }
}
