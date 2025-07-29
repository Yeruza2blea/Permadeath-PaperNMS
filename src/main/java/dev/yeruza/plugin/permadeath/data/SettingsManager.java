package dev.yeruza.plugin.permadeath.data;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.io.File;
import java.io.IOException;

public abstract class SettingsManager {
    protected final Permadeath plugin;

    protected File file;
    protected FileConfiguration config;

    protected SettingsManager(Permadeath plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    protected void saveFile(){
        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected void reloadFile() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
