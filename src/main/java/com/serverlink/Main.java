package com.serverlink;

import com.serverlink.command.ServerLinkCommand;
import com.serverlink.config.ConfigManager;
import com.serverlink.config.LanguageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private ConfigManager configManager;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new ConfigManager();
        configManager.loadAll();
        languageManager = new LanguageManager();
        languageManager.loadLang();

        ServerLinkCommand executor = new ServerLinkCommand();
        getCommand("serverlink").setExecutor(executor);
        getCommand("server").setExecutor(executor);
    }

    public void reloadAll() {
        reloadConfig();
        configManager.loadAll();
        languageManager.loadLang();
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
}
