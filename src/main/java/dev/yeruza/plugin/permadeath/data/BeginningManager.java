package dev.yeruza.plugin.permadeath.data;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeginningManager extends SettingsManager {
    public BeginningManager(Permadeath plugin) {
        super(plugin, new File(plugin.getDataFolder(), "server/the-beginning.yml"));

        if (!file.exists()) {
            try {
                file.createNewFile();
                config.set("config-version", 3);
            } catch (IOException ex) {
                plugin.getLogger().warning("Ha ocurrido un error al crear el archivo 'the-beginning.yml'");
            }
        }

        if (!config.contains("beginning-options.generated-overworld-portal")) {
            config.set("beginning-options.generated-overworld-portal", false);
        }

        if (!config.contains("beginning-options.generated-beginning-portal")) {
            config.set("beginning-options.generated-beginning-portal", false);
        }

        if (!config.contains("beginning-options.overworld-portal-coords")) {
            config.set("beginning-options.overworld-portal-coords", "");
        }


        if (!config.contains("beginning-options.self-portal-coords")) {
            config.set("beginning-options.beginning-portal-coords", "");
        }

        if (!config.contains("beginning-options.killed-ed")) {
            config.set("beginning-options.killed-ed", false);
        }

        if (!config.contains("beginning-options.populated-chests")) {
            config.set("beginning-options.populated-chests", new ArrayList<>());
        }

        saveFile();
        reloadFile();
    }

    public boolean hasPopulatedChest(Location loc) {
        return config.getStringList("beginning-options.populated-chests").contains(TextFormat.parseLocation(loc));
    }

    public void addPopulatedChest(Location loc) {
        List<String> chests = config.getStringList("beginning-options.populated-chests");

        chests.add(TextFormat.parseLocation(loc));

        config.set("beginning-options.populated-chests", chests);
        saveFile();
        reloadFile();
    }

    public void saveFile() {
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("[ERROR] Ha ocurrido un error al guardar el archivo 'players.yml'");
        }
    }

    public boolean generatedOverworldBeginningPortal() {
        return config.getBoolean("beginning-options.generated-overworld-portal");
    }

    public boolean generatedBeginningPortal() {
        return config.getBoolean("beginning-options.generated-beginning-portal");
    }

    public Location getBeginningPortal() {
        if (!generatedBeginningPortal()) {
            return null;
        }

        return TextFormat.parseCoords(config.getString("beginning-options.beginning-portal-coords"));
    }

    public void setOverWorldPortal(Location loc) {

        if (generatedOverworldBeginningPortal()) return;


        config.set("beginning-options.generated-overworld-portal", true);
        config.set("beginning-options.overworld-portal-coords", TextFormat.parseLocation(loc));

        saveFile();
        reloadFile();
    }

    public void setBeginningPortal(Location loc) {

        if (generatedBeginningPortal()) {
            return;
        }

        config.set("beginning-options.generated-beginning-portal", true);
        config.set("beginning-options.beginning-portal-coords", TextFormat.parseLocation(loc));

        saveFile();
        reloadFile();
    }

    public Location getOverworldPortal() {
        if (!generatedBeginningPortal()) {
            return null;
        }

        return TextFormat.parseCoords(config.getString("beginning-options.overworld-portal-coords"));
    }

    public void setKilledED() {
        config.set("killed-ed", true);

        saveFile();
        reloadFile();
    }

    public boolean killedEd() {
        return config.getBoolean("killed-ed");
    }

    public void reloadFile() {
        try {
            config.load(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidConfigurationException ex) {
            plugin.getLogger().warning("Ha ocurrido un error al guardar el archivo 'players.yml'");
        }
    }

    public static String writeCoords(Location loc) {
        return loc.getWorld().getName() + "|" + loc.getX() + "|" + loc.getY() + "|" + loc.getZ();
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
