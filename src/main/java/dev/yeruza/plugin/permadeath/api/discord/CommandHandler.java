package dev.yeruza.plugin.permadeath.api.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandHandler extends ListenerAdapter {


    public static final String PREFIX = "y!";

    @Override
    public void onReady(@NotNull ReadyEvent event) {

    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandInteraction interaction = event.getInteraction();

        String commandName = interaction.getName();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();

        if (!message.getContentRaw().contains(PREFIX) || event.getAuthor().isBot() || event.getAuthor().isSystem())
            return;

        List<String> args = List.of(message.getContentRaw().substring(PREFIX.length()).trim().split(" "));
        String commandName = args.removeFirst().toLowerCase();
    }
}
