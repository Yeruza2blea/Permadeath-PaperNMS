package dev.yeruza.plugin.permadeath.plugin.event;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

public final class LifeOrbEvent extends CustomBossBarEvent {
    public LifeOrbEvent(Permadeath plugin) {
        super(plugin);

        time = 1200 * 8;
        title = TextFormat.write("&60:00 para obtener el orbe de vida");
        bar = Bukkit.createBossBar(title.content(), BarColor.RED, BarStyle.SOLID);
    }
}
