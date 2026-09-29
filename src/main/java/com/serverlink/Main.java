package com.serverlink;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private ServerListManager serverListManager;
    private Command cmdHandler;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        languageManager = new LanguageManager(this);
        serverListManager = new ServerListManager(this);
        cmdHandler = new Command(this);

        releaseLangFiles();
        releaseServerListFile();
        reloadAll();

        getCommand("serverlink").setExecutor(cmdHandler);
        getCommand("serverlink").setTabCompleter(cmdHandler);
        getCommand("server").setExecutor(cmdHandler);
        getCommand("server").setTabCompleter(cmdHandler);

        getLogger().info("ServerLink loaded | Vanilla Transfer (No Proxy) v1.0.3");
    }

    private void releaseLangFiles() {
        var langDir = getDataFolder().toPath().resolve("lang");
        if (!langDir.toFile().exists()) {
            langDir.toFile().mkdirs();
        }
        saveResource("lang/zh_CN.yml", false);
        saveResource("lang/zh_TW.yml", false);
        saveResource("lang/en_US.yml", false);
        saveResource("lang/ru_RU.yml", false);
        saveResource("lang/fr_FR.yml", false);
        saveResource("lang/ja_JP.yml", false);
        saveResource("lang/de_DE.yml", false);
    }

    private void releaseServerListFile() {
        saveResource("serverlist.yml", false);
    }

    public void reloadAll() {
        configManager.loadConfig();
        languageManager.loadLanguage();
        serverListManager.loadServerList();
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public ServerListManager getServerListManager() {
        return serverListManager;
    }
}