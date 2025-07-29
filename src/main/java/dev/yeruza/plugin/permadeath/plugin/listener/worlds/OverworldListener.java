package dev.yeruza.plugin.permadeath.plugin.listener.worlds;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

public class OverworldListener implements Listener {
    private final Permadeath plugin;

    public OverworldListener(Permadeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherStorm(WeatherChangeEvent event) {

        if (!event.toWeatherState()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                TextComponent msg = TextFormat.showWithPrefix(plugin.getMessageData().getMessage("storm-end", player));
                player.sendMessage(msg);
            }

            plugin.getMessageData().sendConsole("storm-end", value -> value);

            if (plugin.getDay() >= 50) {
                if (plugin.getBeginning() != null) {
                    plugin.getBeginning().setClosed(false);
                }

                for (World world : Bukkit.getWorlds()) {
                    world.setGameRule(GameRule.NATURAL_REGENERATION, true);
                }
            }
        } else {
            if (event.getWorld().getEnvironment() == World.Environment.NORMAL && plugin.getDay() >= 25) {
                for (World world : Bukkit.getWorlds()) {
                    for (LivingEntity entity : world.getLivingEntities()) {
                        plugin.addDeathTrainEffects(entity);
                    }
                }
            }
        }
    }
}
