package dev.yeruza.plugin.permadeath.plugin.listener.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.gaming.scoreboard.ScoreboardMutable;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

public class DetailsListener implements Listener {
    private static final TextComponent HEALTH_TEXT = Component.text("Health", NamedTextColor.RED, TextDecoration.BOLD);

    private final Permadeath plugin;

    private final Scoreboard mainBoard = ScoreboardMutable.createNewScoreboard();

    public DetailsListener(Permadeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {


        Objective health = mainBoard.registerNewObjective("permadeath_health", Criteria.HEALTH, HEALTH_TEXT, RenderType.HEARTS);
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);



        event.getPlayer().setScoreboard(mainBoard);
    }
}
