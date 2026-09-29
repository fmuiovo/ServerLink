package com.serverlink;

import org.bukkit.entity.Player;

public class TransferCore {
    public static boolean transferByAlias(Player player, String alias) {
        var slm = Main.getInstance().getServerListManager();
        if (!slm.hasServer(alias)) return false;

        String host = slm.getHost(alias);
        int port = slm.getPort(alias);
        try {
            player.transfer(host, port);
            return true;
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Transfer failed: " + e.getMessage());
            return false;
        }
    }
}