package com.serverlink.config;

import com.serverlink.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class LanguageManager {
    private FileConfiguration langConfig;

    private final List<String> builtinLangs = List.of(
            "zh_CN",
            "zh_TW",
            "en_US",
            "de_DE",
            "es_ES",
            "fr_FR",
            "ja_JP",
            "pt_BR",
            "ru_RU"
    );

    public void saveAllLanguages() {
        File langFolder = new File(Main.getInstance().getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        for (String langId : builtinLangs) {
            String resPath = "lang/" + langId + ".yml";
            File outFile = new File(langFolder, langId + ".yml");
            if (!outFile.exists()) {
                Main.getInstance().saveResource(resPath, false);
            }
        }
    }

    public void loadLang() {
        String langId = Main.getInstance().getConfig().getString("language", "zh_CN");
        File langFolder = new File(Main.getInstance().getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();

        File langFile = new File(langFolder, langId + ".yml");
        if (!langFile.exists()) {
            Main.getInstance().getLogger().warning("Language file " + langId + ".yml not found, fallback to en_US");
            langFile = new File(langFolder, "en_US.yml");
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

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