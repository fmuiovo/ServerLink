package com.serverlink;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {
    private final Main plugin;
    private FileConfiguration langConfig;
    private FileConfiguration fallbackConfig;

    public LanguageManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadLanguage() {
        String langName = plugin.getConfigManager().getConfig().getString("language", "en_US");
        File langDir = new File(plugin.getDataFolder(), "lang");
        File langFile = new File(langDir, langName + ".yml");

        if (langFile.exists()) {
            langConfig = YamlConfiguration.loadConfiguration(langFile);
        } else {
            plugin.getLogger().warning("Language file " + langName + ".yml not found, fallback to en_US");
            langFile = new File(langDir, "en_US.yml");
            langConfig = langFile.exists()
                    ? YamlConfiguration.loadConfiguration(langFile)
                    : loadResourceLang("lang/en_US.yml");
        }

        fallbackConfig = loadResourceLang("lang/en_US.yml");
    }

    private FileConfiguration loadResourceLang(String resourcePath) {
        InputStream is = plugin.getResource(resourcePath);
        if (is == null) return new YamlConfiguration();
        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load built-in lang resource: " + resourcePath);
            e.printStackTrace();
            return new YamlConfiguration();
        }
    }

    public String getMessage(String key) {
        String msg = langConfig.getString(key, fallbackConfig.getString(key));
        if (msg == null) return "§cMissing key: " + key;
        return msg.replace("&", "§");
    }

    public String getMessage(String key, String... replace) {
        String msg = getMessage(key);
        for (int i = 0; i < replace.length; i += 2) {
            if (i + 1 >= replace.length) break;
            msg = msg.replace(replace[i], replace[i + 1]);
        }
        return msg;
    }

    public void reload() {
        loadLanguage();
    }
}