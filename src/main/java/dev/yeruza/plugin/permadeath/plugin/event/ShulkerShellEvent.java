package dev.yeruza.plugin.permadeath.plugin.event;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.event.Cancellable;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

public final class ShulkerShellEvent extends CustomBossBarEvent implements Cancellable {
    private boolean cancelled;

    public ShulkerShellEvent(Permadeath plugin) {
        super(plugin);

        time = 1200 * 4;
        title = TextFormat.write("&c&lX2 Shullkers Shells: &b&n");
        bar = Bukkit.createBossBar(title.content(), BarColor.RED, BarStyle.SOLID);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
