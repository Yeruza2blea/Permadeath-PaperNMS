package dev.yeruza.plugin.permadeath.data;

import dev.yeruza.plugin.permadeath.Permadeath;

import java.io.File;
import java.io.IOException;

public class DataBaseManager extends SettingsManager {
    public DataBaseManager(Permadeath plugin) {
        super(plugin, new File(plugin.getDataFolder(), "database.yml"));

        if (!file.exists()) {
            try {
                file.createNewFile();
                config.set("config-version", 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!config.contains("path-final")) {
            config.set("path-final", "mongodb+srv://{username}:{password}@serverdb.eg0wd.mongodb.net/?retryWrites=true&w=majority&appName={app-name}");
        }

        if (!config.contains("database.username")) {
            config.set("database.username", "");
        }

        if (!config.contains("database.password")) {
            config.set("database.password", "");
        }

        if (!config.contains("database.host")) {
            config.set("database.host", "");
        }

        if (!config.contains("database.app-name")) {
            config.set("database.app-name", "");
        }

        saveFile();
        reloadFile();
    }

    public String getName() {
        return config.getString("database.username");
    }

    public String getPassword() {
        return config.getString("database.password");
    }

    public String getHost() {
        return config.getString("database.host");
    }

    public String getAppName() {
        return config.getString("database.app-name");
    }
}
