package dev.yeruza.plugin.permadeath.data;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.Language;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

public class MessageManager extends SettingsManager {
    private final Language lang;

    public MessageManager(Permadeath plugin) {
        super(plugin, new File(plugin.getDataFolder(), "lang/message/general.yml"));

        if (!file.exists()) {
            try {
                file.createNewFile();
                config.set("config-version", 3);

            } catch (IOException ex) {
                plugin.getLogger().severe("Ha ocurrido un error al crear el archivo 'general.yml'");
            }
        }

        config.set("config-version", 3);
        lang = Language.SPANISH;
        createData(lang);

        saveFile();
        reloadFile();
    }


    private void createData(Language lang) {
        switch (lang) {
            case SPANISH -> {
                config.set("server.spanish.on-join", "&a{player} &7se ha unido al servidor");
                config.set("server.spanish.on-leave", "&a{player} &7se ha salido del servidor");
                config.set("server.spanish.storm-end", "&7La tormenta ha llegado a su fin de momento");
                config.set("server.spanish.sleep", "&a{player} &7fue a dormir el pana");
                config.set("server.spanish.sleeping", "&a{player} &7está duermiendo &e(&b{player_size}/&b{needed})");
                config.set("server.spanish.death-title", "&c&l¡PERMADEATH!");
                config.set("server.spanish.death-subtitle", "&6&l{player} &fha muerto");
                config.set("server.spanish.death-chat", "&c&lEste es el comienzo del sufrimiento eterno de &4&l{player}&c&l. ¡HA SIDO PERMABANEADO!");
                config.set("server.spanish.death-train-message", "&c¡Comienza el Death Train con duración de {time_left} horas!");
                config.set("server.spanish.death-train-message-time", "&c¡Comienza el Death Train con duración de {time_left} minutos!");
                config.set("server.spanish.action-bar-message", "&e&lQuedan &f&l{time_left}&e&l de tormenta");
            }
            case ENGLISH -> {
                config.set("server.english.on-join", "{player} joined the game");
                config.set("server.english.on-leave", "{player} left the game");
                config.set("server.english.storm-end", "The storm has ended");
                config.set("server.english.sleep", "&7{player} &ejust went to sleep, Sweet dreams!");
                config.set("server.english.sleeping", "&7%player% &eis now sleeping &e(&b{players_size}&7/&b{needed}&e)");
                config.set("server.english.death-title", "&c&l¡PERMADEATH!");
                config.set("server.english.death-subtitle", "{player} died");
                config.set("server.english.death-chat", "&c&lThis is the beginning of &4&l{player}&7's eternal suffering. &c&lTHEY'VE BEEN PERMA-BANNED!");
                config.set("server.english.death-train-message", "&cThe Death Train is now ongoing for {time_left} hours!");
                config.set("server.english.death-train-message-time", "&cThe Death Train is now ongoing for {time_left} minutes!");
                config.set("server.english.action-bar-message", "&7The storm will end on {time_left}");
            }
        }
    }

    public TextComponent getMessageByPlayer(String path, Player player, UnaryOperator<String> context) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUniqueId());
        PlayerManager data = new PlayerManager(offline, plugin);

        String message = config.getString(path.replace("{lang}", lang.name().toLowerCase()));
        String result = context.apply(message);

        return TextFormat.write(result != null ? result : "");
    }

    public TextComponent getMessageByConsole(String path) {
        String message = config.getString(path);

        if (message == null)
            return TextFormat.write("Sin Tiempo");

        return TextFormat.write(message);
    }

    public TextComponent getMessage(String path, Player player, UnaryOperator<String> context) {
        return getMessageByPlayer("server.spanish." + path, player, context);
    }

    public String getMessage(String path, Player player) {


        return getMessageByPlayer("server." + lang.name().toLowerCase() + "." + path, player, UnaryOperator.identity()).content();
    }


    public void sendConsole(String path, UnaryOperator<String> context) {
        TextComponent format = getMessageByConsole(String.format("server.%s.%s", lang.name().toLowerCase(), path));
        String message = context.apply(format.content());

        Bukkit.getConsoleSender().sendMessage(message);
    }
}
