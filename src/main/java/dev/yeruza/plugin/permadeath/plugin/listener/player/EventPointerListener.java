package dev.yeruza.plugin.permadeath.plugin.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.event.LifeOrbEvent;
import dev.yeruza.plugin.permadeath.plugin.event.ShulkerShellEvent;

public record EventPointerListener(Permadeath plugin) implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShulkerShellEvent(ShulkerShellEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, Float.MAX_VALUE, -1);
            event.addPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLifeOrbEvent(LifeOrbEvent event) {

    }
}
