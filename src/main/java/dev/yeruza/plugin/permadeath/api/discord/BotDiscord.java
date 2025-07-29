package dev.yeruza.plugin.permadeath.api.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.OfflinePlayer;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.data.PlayerManager;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BotDiscord extends JDABuilder implements AutoCloseable {
    private final JDA jda = build();

    public static final Map<String, String> SLASH_COMMANDS = new LinkedHashMap<>(100);
    public static final Map<String, String> MESSAGE_COMMANDS = new HashMap<>(100);

    public BotDiscord(String token) {
        super(token, GatewayIntent.ALL_INTENTS);


        setStatus(OnlineStatus.DO_NOT_DISTURB);
        setActivity(Activity.customStatus(Permadeath.getPlugin().getDay() + " hasta el día 120"));
        applyDefault();
    }

    public void start() throws InterruptedException {
        jda.addEventListener(new CommandHandler());
    }

    @Override
    public void close() throws InterruptedException {
        jda.awaitReady();
    }

    public JDA getJDA() {
        return build();
    }

    public void applyBanUser(OfflinePlayer user, String reason, boolean isForAFK) {
        PlayerManager manager = new PlayerManager(user, Permadeath.getPlugin());
        TextChannel channel = jda.getTextChannelById("");

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("El jugador" + manager.player.getName() + "ha sido PERMABANEADO")
                .setTimestamp(OffsetDateTime.now())
                .build();


        channel.sendMessageEmbeds(embed).queue(message -> {
            message.createThreadChannel("F POR FIRE PIBE XD");
            message.addReaction(Emoji.fromFormatted("☠"));
        });

    }

    public void applyUnbanUser(OfflinePlayer user, String reason) {

    }
}
