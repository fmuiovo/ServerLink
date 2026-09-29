package com.serverlink;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ServerListManager {
    private final Main plugin;
    private File listFile;
    private FileConfiguration listConfig;

    public ServerListManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadServerList() {
        listFile = new File(plugin.getDataFolder(), "serverlist.yml");
        if (!listFile.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource("serverlist.yml", false);
        }
        listConfig = YamlConfiguration.loadConfiguration(listFile);

        try (InputStream defStream = plugin.getResource("serverlist.yml")) {
            if (defStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, StandardCharsets.UTF_8));
                listConfig.setDefaults(defConfig);
            }
        } catch (Exception ignored) {}
    }

    public FileConfiguration getConfig() {
        return listConfig;
    }

    public void save() {
        try {
            listConfig.save(listFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Save serverlist.yml failed: " + e.getMessage());
        }
    }

    public Set<String> getServerAliases() {
        var sec = listConfig.getConfigurationSection("servers");
        if(sec == null) return Set.of();
        return sec.getKeys(false);
    }

    public boolean hasServer(String alias) {
        return listConfig.contains("servers." + alias);
    }

    public String getHost(String alias) {
        return listConfig.getString("servers." + alias + ".host");
    }

    public int getPort(String alias) {
        return listConfig.getInt("servers." + alias + ".port");
    }

    public void addServer(String alias, String host, int port) {
        listConfig.set("servers." + alias + ".host", host);
        listConfig.set("servers." + alias + ".port", port);
        save();
    }

    public void removeServer(String alias) {
        listConfig.set("servers." + alias, null);
        save();
    }
}