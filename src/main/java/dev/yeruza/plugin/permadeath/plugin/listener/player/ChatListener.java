package dev.yeruza.plugin.permadeath.plugin.listener.player;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;
import org.jetbrains.annotations.NotNull;

public class ChatListener implements Listener, ChatRenderer {
    public static final TextComponent FORMAT_CHAT = Component.text("{rank_char_code} {player} {medals} : {message}");
    public static final TextComponent SPACE_CHAT = Component.empty().appendSpace().append(Component.text(':').appendSpace());

    private final Permadeath plugin;

    public ChatListener(Permadeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();



        event.renderer(this);


    }

    @Override
    @NotNull
    public Component render(Player source, Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        TextComponent data = Component.empty()
                .append(TextFormat.write("&f&lPerfil de &6&l" + source.getName()).appendNewline()
                .append(Component.text("Nombre: " + source.getName(), NamedTextColor.RED)).appendNewline()
                .append(Component.text("Rango: " + 'x', NamedTextColor.RED)).appendNewline()
                .append(Component.text("XP: " + source.getExp(), NamedTextColor.RED)));

        Component format = sourceDisplayName.color(NamedTextColor.RED).hoverEvent(HoverEvent.showText(data));
        Component output = message.color(NamedTextColor.GRAY);

        return Component.textOfChildren(
            format,
            SPACE_CHAT,
            output
        );
    }
}
