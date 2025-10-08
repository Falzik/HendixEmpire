package me.falzik.work.hendixempire.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigManager {

    public static final ConfigManager instance = new ConfigManager();

    private final static HashMap<String, YamlConfiguration> configByName = new HashMap<>();

    public void configSetup(String configName, JavaPlugin javaPlugin) {
        File file = new File(javaPlugin.getDataFolder().getAbsolutePath() + "/", configName);
        if(!file.exists()) {
            javaPlugin.saveResource(configName, false);
        }
        configByName.put(configName, YamlConfiguration.loadConfiguration(file));
    }

    public void saveConfig(String configName, JavaPlugin javaPlugin) {
        File file = new File(javaPlugin.getDataFolder().getAbsolutePath() + "/", configName);
        try {
            getConfig(configName).save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public YamlConfiguration getConfig(String configName) {
        return configByName.get(configName);
    }


}
