package com.serverlink;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Command implements CommandExecutor, TabCompleter {
    private final Main plugin;
    private static final Set<String> VALID_LANG_CODES = Set.of(
            "zh_CN",
            "zh_TW",
            "en_US",
            "ja_JP",
            "ru_RU",
            "fr_FR",
            "de_DE"
    );

    public Command(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        // /server 主命令分支
        if (command.getName().equalsIgnoreCase("server")) {
            // 不带参数 /server → 直接执行 list
            if (args.length == 0) {
                Set<String> aliases = plugin.getServerListManager().getServerAliases();
                if (aliases.isEmpty()) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("list-empty"));
                    return true;
                }
                sender.sendMessage(plugin.getLanguageManager().getMessage("list-header"));
                for (String s : aliases) {
                    String host = plugin.getServerListManager().getHost(s);
                    int port = plugin.getServerListManager().getPort(s);
                    sender.sendMessage(plugin.getLanguageManager().getMessage("list-item", "%name%", s, "%host%", host, "%port%", String.valueOf(port)));
                }
                return true;
            }
            // /server <别名> 跳转
            if (!sender.hasPermission("serverlink.transfer")) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
                return true;
            }
            if (args.length == 1) {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("player-only"));
                    return true;
                }
                String alias = args[0];
                boolean ok = TransferCore.transferByAlias(p, alias);
                if (!ok) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("server-not-exist", "%server%", alias));
                    return true;
                }
                sender.sendMessage(plugin.getLanguageManager().getMessage("transfer-send", "%server%", alias));
                return true;
            }
        }

        // ===== /serverlink 主命令 =====
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-header"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-help"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-transfer"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-server-quick"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-optransfer"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-list"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-server-add"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-server-remove"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-config"));
            sender.sendMessage(plugin.getLanguageManager().getMessage("help-reload"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("serverlink.reload")) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
                return true;
            }
            plugin.reloadAll();
            sender.sendMessage(plugin.getLanguageManager().getMessage("reload-success"));
            return true;
        }

        if (args[0].equalsIgnoreCase("config")) {
            // /serverlink config config.language <langId>
            if (args.length >= 3 && args[1].equalsIgnoreCase("config.language")) {
                if (!sender.hasPermission("serverlink.reload")) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
                    return true;
                }
                String targetLang = args[2];
                if (!VALID_LANG_CODES.contains(targetLang)) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("invalid-language"));
                    return true;
                }
                plugin.getConfigManager().getConfig().set("language", targetLang);
                plugin.getConfigManager().saveConfig();
                plugin.reloadAll();
                sender.sendMessage(plugin.getLanguageManager().getMessage("set-language-success", "%lang%", targetLang));
                return true;
            }
            sender.sendMessage(plugin.getLanguageManager().getMessage("config-tip"));
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            Set<String> aliases = plugin.getServerListManager().getServerAliases();
            if (aliases.isEmpty()) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("list-empty"));
                return true;
            }
            sender.sendMessage(plugin.getLanguageManager().getMessage("list-header"));
            for (String s : aliases) {
                String host = plugin.getServerListManager().getHost(s);
                int port = plugin.getServerListManager().getPort(s);
                sender.sendMessage(plugin.getLanguageManager().getMessage("list-item", "%name%", s, "%host%", host, "%port%", String.valueOf(port)));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("transfer")) {
            if (!sender.hasPermission("serverlink.transfer")) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
                return true;
            }
            if (args.length == 2) {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("player-only"));
                    return true;
                }
                String alias = args[1];
                boolean ok = TransferCore.transferByAlias(p, alias);
                if (!ok) {
                    sender.sendMessage(plugin.getLanguageManager().getMessage("server-not-exist", "%server%", alias));
                    return true;
                }
                sender.sendMessage(plugin.getLanguageManager().getMessage("transfer-send", "%server%", alias));
                return true;
            }
            sender.sendMessage(plugin.getLanguageManager().getMessage("transfer-usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("optransfer")) {
            if (!sender.hasPermission("serverlink.optransfer")) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
                return true;
            }
            if (args.length != 3) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("optransfer-usage"));
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("player-not-online", "%player%", args[1]));
                return true;
            }
            String alias = args[2];
            boolean ok = TransferCore.transferByAlias(target, alias);
            if (!ok) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("server-not-exist", "%server%", alias));
                return true;
            }
            sender.sendMessage(plugin.getLanguageManager().getMessage("transfer-send-other", "%player%", target.getName(), "%server%", alias));
            return true;
        }

        if (args[0].equalsIgnoreCase("server")) {
            if (!sender.hasPermission("serverlink.server")) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
                return true;
            }
            if (args.length >= 2) {
                if (args[1].equalsIgnoreCase("add")) {
                    if (args.length != 5) {
                        sender.sendMessage(plugin.getLanguageManager().getMessage("server-add-usage"));
                        return true;
                    }
                    String name = args[2];
                    String host = args[3];
                    int port;
                    try {
                        port = Integer.parseInt(args[4]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(plugin.getLanguageManager().getMessage("port-invalid"));
                        return true;
                    }
                    if (plugin.getServerListManager().hasServer(name)) {
                        sender.sendMessage(plugin.getLanguageManager().getMessage("server-already-exist", "%name%", name));
                        return true;
                    }
                    plugin.getServerListManager().addServer(name, host, port);
                    sender.sendMessage(plugin.getLanguageManager().getMessage("server-add-success", "%name%", name));
                    return true;
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length != 3) {
                        sender.sendMessage(plugin.getLanguageManager().getMessage("server-remove-usage"));
                        return true;
                    }
                    String name = args[2];
                    if (!plugin.getServerListManager().hasServer(name)) {
                        sender.sendMessage(plugin.getLanguageManager().getMessage("server-not-exist", "%server%", name));
                        return true;
                    }
                    plugin.getServerListManager().removeServer(name);
                    sender.sendMessage(plugin.getLanguageManager().getMessage("server-remove-success", "%name%", name));
                    return true;
                }
            }
            sender.sendMessage(plugin.getLanguageManager().getMessage("server-sub-usage"));
            return true;
        }

        sender.sendMessage(plugin.getLanguageManager().getMessage("unknown-command"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> candidates = new ArrayList<>();
        Set<String> aliasList = plugin.getServerListManager().getServerAliases();

        if (command.getName().equalsIgnoreCase("server")) {
            if (args.length == 1) {
                candidates.addAll(aliasList);
            }
            return StringUtil.copyPartialMatches(args[args.length - 1], candidates, completions);
        }

        if (args.length == 1) {
            candidates.add("help");
            candidates.add("reload");
            candidates.add("config");
            candidates.add("list");
            candidates.add("transfer");
            candidates.add("optransfer");
            candidates.add("server");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("transfer")) {
                candidates.addAll(aliasList);
            } else if (args[0].equalsIgnoreCase("optransfer")) {
                for (Player p : Bukkit.getOnlinePlayers()) candidates.add(p.getName());
            } else if (args[0].equalsIgnoreCase("server")) {
                candidates.add("add");
                candidates.add("remove");
            } else if (args[0].equalsIgnoreCase("config")) {
                candidates.add("config.language");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("optransfer")) {
                candidates.addAll(aliasList);
            } else if (args[0].equalsIgnoreCase("server") && args[1].equalsIgnoreCase("remove")) {
                candidates.addAll(aliasList);
            } else if (args[0].equalsIgnoreCase("config") && args[1].equalsIgnoreCase("config.language")) {
                candidates.addAll(VALID_LANG_CODES);
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("server") && args[1].equalsIgnoreCase("add")) {
            candidates.add("127.0.0.1");
        } else if (args.length == 5 && args[0].equalsIgnoreCase("server") && args[1].equalsIgnoreCase("add")) {
            candidates.add("25565");
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], candidates, completions);
    }
}