package dev.yeruza.plugin.permadeath.api.commands;

import dev.yeruza.plugin.permadeath.utils.TextFormat;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandHelper {
    private static final Server server = Bukkit.getServer();
    private static final LegacyComponentSerializer legacyAmpersand = LegacyComponentSerializer.legacyAmpersand();
    public static CommandHelper callTarget(Player target) {
        return new CommandHelper(target);
    }

    private final Player player;
    private Player.Spigot spigot;

    CommandHelper(Player player) {
        this.player = player;
    }

    CommandHelper(UUID id) {
        this.player = server.getPlayer(id);
    }

    public void sendMessage(String content) {

        player.sendMessage(legacyAmpersand.deserialize(content));
    }

    public void sendMessage(TextComponent text) {
        player.sendMessage(text);
    }

    public void sendMessage(String ...content) {


        player.sendMessage(legacyAmpersand.deserialize(""));
    }

    public void sendBarMessage(String content) {
        player.sendActionBar(legacyAmpersand.deserialize(content));
    }
}
