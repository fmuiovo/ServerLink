package com.serverlink.command;

import com.serverlink.Main;
import com.serverlink.config.ConfigManager;
import com.serverlink.config.LanguageManager;
import com.serverlink.util.TransferUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Set;

public class ServerLinkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        LanguageManager lang = Main.getInstance().getLanguageManager();
        ConfigManager configManager = Main.getInstance().getConfigManager();
        FileConfiguration cmdCfg = configManager.getCommandsConfig();
        FileConfiguration serverDataCfg = configManager.getServerDataConfig();
        FileConfiguration mainCfg = Main.getInstance().getConfig();

        if (command.getName().equalsIgnoreCase("serverlink")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                if (!sender.hasPermission("serverlink.help")) {
                    sender.sendMessage(lang.get("message.no_permission"));
                    return true;
                }
                sender.sendMessage(lang.get("help_title"));
                for (String line : lang.getLangConfig().getStringList("help_lines")) {
                    sender.sendMessage(line);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("connect")) {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage(lang.get("message.only_player"));
                    return true;
                }
                if (!sender.hasPermission("serverlink.connect")) {
                    sender.sendMessage(lang.get("message.no_permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(lang.get(cmdCfg.getString("usage.connect")));
                    return true;
                }
                String srv = args[1];
                handleServerConnect(p, srv, lang, configManager, serverDataCfg, mainCfg);
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (!sender.hasPermission("serverlink.list")) {
                    sender.sendMessage(lang.get("message.no_permission"));
                    return true;
                }
                sender.sendMessage(lang.get("list_header"));
                Set<String> keys = serverDataCfg.getConfigurationSection("servers") != null
                        ? serverDataCfg.getConfigurationSection("servers").getKeys(false)
                        : Set.of();
                for (String name : keys) {
                    String h = serverDataCfg.getString("servers." + name + ".host");
                    int prt = serverDataCfg.getInt("servers." + name + ".port");
                    sender.sendMessage(lang.getFormat("list_item", name, h, prt));
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("serverlink.add")) {
                    sender.sendMessage(lang.get("message.no_permission"));
                    return true;
                }
                if (args.length < 4) {
                    sender.sendMessage(lang.get(cmdCfg.getString("usage.add")));
                    return true;
                }
                String name = args[1];
                String h = args[2];
                int prt;
                try {
                    prt = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§c端口必须为数字");
                    return true;
                }
                if (serverDataCfg.contains("servers." + name)) {
                    sender.sendMessage(lang.get("message.server_already_exists"));
                    return true;
                }
                serverDataCfg.set("servers." + name + ".host", h);
                serverDataCfg.set("servers." + name + ".port", prt);
                serverDataCfg.set("servers." + name + ".proxy-name", name);
                configManager.saveServerData();
                sender.sendMessage(lang.get("message.server_added"));
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("serverlink.remove")) {
                    sender.sendMessage(lang.get("message.no_permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(lang.get(cmdCfg.getString("usage.remove")));
                    return true;
                }
                String name = args[1];
                if (!serverDataCfg.contains("servers." + name)) {
                    sender.sendMessage(lang.get("message.server_not_found"));
                    return true;
                }
                serverDataCfg.set("servers." + name, null);
                configManager.saveServerData();
                sender.sendMessage(lang.get("message.server_removed"));
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("serverlink.reload")) {
                    sender.sendMessage(lang.get("message.no_permission"));
                    return true;
                }
                Main.getInstance().reloadAll();
                sender.sendMessage(lang.get("message.reload_success"));
                return true;
            }

            sender.sendMessage(lang.get(cmdCfg.getString("unknown_command")));
            return true;
        }

        if (command.getName().equalsIgnoreCase("server")) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(lang.get("message.only_player"));
                return true;
            }
            if (!sender.hasPermission("serverlink.connect")) {
                sender.sendMessage(lang.get("message.no_permission"));
                return true;
            }
            if (args.length < 1) {
                sender.sendMessage(lang.get(cmdCfg.getString("usage.server")));
                return true;
            }
            String srv = args[0];
            handleServerConnect(p, srv, lang, configManager, serverDataCfg, mainCfg);
            return true;
        }
        return false;
    }

    private void handleServerConnect(Player p, String srv, LanguageManager lang, ConfigManager configManager,
                                     FileConfiguration serverDataCfg, FileConfiguration mainCfg) {
        String host = serverDataCfg.getString("servers." + srv + ".host");
        int port = serverDataCfg.getInt("servers." + srv + ".port");
        String proxyName = serverDataCfg.getString("servers." + srv + ".proxy-name");

        if (host == null) {
            p.sendMessage(lang.get("message.server_not_found"));
            return;
        }

        String mode = mainCfg.getString("transfer-mode", "PAPER");
        if (mode.equalsIgnoreCase("PAPER")) {
            if (!TransferUtil.supportPaperTransfer()) {
                p.sendMessage(lang.get("message.spigot_not_support"));
                return;
            }
            try {
                TransferUtil.paperTransfer(p, host, port);
                p.sendMessage(lang.getFormat("message.transfer_success", srv));
            } catch (Exception e) {
                p.sendMessage(lang.get("message.transfer_fail"));
            }
        } else if (mode.equalsIgnoreCase("PROXY")) {
            if (!p.getServer().getMessenger().isOutgoingChannelRegistered(Main.getInstance(), "BungeeCord")) {
                p.sendMessage(lang.get("message.proxy_no_proxy"));
                return;
            }
            boolean ok = TransferUtil.proxyTransfer(p, proxyName);
            if (ok) {
                p.sendMessage(lang.getFormat("message.transfer_success", srv));
            } else {
                p.sendMessage(lang.get("message.proxy_send_fail"));
            }
        } else {
            p.sendMessage("§c无效的transfer‑mode配置！");
        }
    }
}
