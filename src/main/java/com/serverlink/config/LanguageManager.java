package com.serverlink.config;

import com.serverlink.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LanguageManager {
    private FileConfiguration langConfig;

    public void loadLang() {
        String langId = Main.getInstance().getConfig().getString("language", "zh_CN");
        File langFolder = new File(Main.getInstance().getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();
        File langFile = new File(langFolder, langId + ".yml");
        if (!langFile.exists()) {
            Main.getInstance().saveResource("lang/" + langId + ".yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    // 新增对外获取langConfig
    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    public String get(String path) {
        return langConfig.getString(path, "missing:" + path);
    }

    public String getFormat(String path, Object... args) {
        return String.format(get(path), args);
    }
}
