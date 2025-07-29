package dev.yeruza.plugin.permadeath.data;

import org.bukkit.configuration.InvalidConfigurationException;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EndManager extends SettingsManager {
    private List<Integer> time;

    public EndManager(Permadeath plugin) {
        super(plugin, new File(plugin.getDataFolder(), "server/end-config.yml"));

        if (!file.exists()) {
            try {
                file.createNewFile();
                config.set("config-version", 3);
            } catch (IOException ex) {
                plugin.getLogger().severe("Ha ocurrido un error al crear el archivo 'end-config.yml'");
            }
        }

        if (!config.contains("end-options.ender-crystal-regen-time")) {
            config.setComments("end-crystal-regen-time", List.of("La siguiente es una lista de números en segundos del tiempo que toma regenerar un End Crystal."));
            config.set("end-options.end-crystal-regen-time", List.of(60, 90, 120, 30, 240, 150));
        }

        if (!config.contains("end-options.placed-obsidian")) {
            config.set("end-options.placed-obsidian", new ArrayList<>());
        }

        if (!config.contains("end-options.replaced-obsidian")) {
            config.set("end-options.replaced-obsidian", true);
        }

        if (!config.contains("end-options.created-regen-zone")) {
            config.set("end-options.created-regen-zone", false);
        }

        if (!config.contains("end-options.decorated-end-spawn")) {
            config.set("end-options.decorated-end-spawn", true);
        }

        saveFile();
        reloadFile();

        loadSettings();
    }


    public void loadSettings() {
        time = config.getIntegerList("end-options.end-crystal-regen-time");
    }

    public List<Integer> getTimeList() {
        return time;
    }

    public void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Ha ocurrido un error al guardar el archivo 'end-config.yml'");
        }
    }

    public void reloadFile() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Ha ocurrido un error al guardar el archivo 'end-config.yml'");
        }

        loadSettings();
    }
}
