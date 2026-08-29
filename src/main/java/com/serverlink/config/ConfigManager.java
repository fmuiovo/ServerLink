package com.serverlink.config;

import com.serverlink.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private File commandsFile;
    private FileConfiguration commandsConfig;

    private File serverDataFile;
    private FileConfiguration serverDataConfig;

    public void loadAll() {
        File dataFolder = Main.getInstance().getDataFolder();

        commandsFile = new File(dataFolder, "commands.yml");
        if (!commandsFile.exists()) {
            Main.getInstance().saveResource("commands.yml", false);
        }
        commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);

        serverDataFile = new File(dataFolder, "servers-data.yml");
        if (!serverDataFile.exists()) {
            try {
                boolean created = serverDataFile.createNewFile();
                if (!created) {
                    Main.getInstance().getLogger().severe("无法创建servers-data.yml！");
                }
            } catch (IOException e) {
                Main.getInstance().getLogger().severe("无法创建servers-data.yml！");
            }
        }
        serverDataConfig = YamlConfiguration.loadConfiguration(serverDataFile);
    }

    public void saveServerData() {
        try {
            serverDataConfig.save(serverDataFile);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("保存servers-data.yml失败:" + e.getMessage());
        }
    }

    public FileConfiguration getCommandsConfig() {
        return commandsConfig;
    }

    public FileConfiguration getServerDataConfig() {
        return serverDataConfig;
    }
}