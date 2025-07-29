package dev.yeruza.plugin.permadeath.data;

import dev.yeruza.plugin.permadeath.Permadeath;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TabListManager extends SettingsManager {
    private String tag;

    private List<String> header, footer;

    public TabListManager(Permadeath plugin) {
        super(plugin, new File(plugin.getDataFolder(), "lang/server/tablist.yml"));
        config.options().parseComments(true);

        config.get("server.options.tablist." + tag);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                plugin.getLogger().warning("Ha ocurrido un error al crear el archivo 'players.yml'");

            }
        }

        if (!config.contains("server.options.tablist.main.header")) {
            List<String> defaultHeader = List.of(
                    " ",
                    "#ff0000&lPermadeath",
                    "Jugadores: {player_size} - Ping: {player_ping}",
                    " ",
                    "Estamos en el día: &c&l{day}",
                    " "
            );

            config.set("server.options.tablist.main.header", defaultHeader);
        }

        if (!config.contains("server.options.tablist.main.footer")) {
            List<String> defaultFooter = List.of(
                    " ",
                    "",
                    " "
            );

            config.set("server.options.tablist.main.footer", defaultFooter);
        }

        header = config.getStringList("server.options.tablist." + tag + ".header");
        footer = config.getStringList("server.options.tablist." + tag + ".footer");

        saveFile();
        reloadFile();
    }

    public List<String> getHeader() {
        return header;
    }

    public List<String> getFooter() {
        return footer;
    }
}
