package dev.yeruza.plugin.permadeath.plugin.listener.worlds;

import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import dev.yeruza.plugin.permadeath.Permadeath;

public class NetherWorldListener implements Listener {
    private final Permadeath plugin;

    public NetherWorldListener(Permadeath plugin) {
        this.plugin = plugin;
    }

    public void onMeanwhileWeatherStorm(WeatherChangeEvent event) {

    }
}
