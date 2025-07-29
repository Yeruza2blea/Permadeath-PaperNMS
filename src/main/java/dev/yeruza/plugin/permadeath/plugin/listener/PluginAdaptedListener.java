package dev.yeruza.plugin.permadeath.plugin.listener;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.lang.reflect.Method;

public abstract class PluginAdaptedListener implements Listener {
    protected final Permadeath plugin;

    public PluginAdaptedListener(@NotNull Permadeath plugin) {
        this.plugin = plugin;
    }

    public void handleEvents() {

        for (Method method : this.getClass().getDeclaredMethods()) {

        }
    }
}
