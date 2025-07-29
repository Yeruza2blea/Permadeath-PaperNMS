package dev.yeruza.plugin.permadeath.plugin.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.List;


public abstract class CustomBossBarEvent extends Event {
    private static final HandlerList handler = new HandlerList();

    protected Permadeath plugin;
    private boolean running;

    protected int time;
    protected BossBar bar;

    protected BarColor color;
    protected BarStyle style;
    protected BarFlag flag;
    protected TextComponent title;

    protected List<Player> players;

    protected CustomBossBarEvent(Permadeath plugin) {
        this.plugin = plugin;
    }

    protected CustomBossBarEvent(Permadeath plugin, List<Player> players) {
        this.plugin = plugin;
        this.players = players;

        color = bar.getColor();
        style = bar.getStyle();
        flag = null;
    }

    public void addPlayers() {
        for (Player player : players)
            bar.addPlayer(player);
    }

    public void addPlayer(Player p) {
        bar.addPlayer(p);
    }

    public void clearPlayers() {
        for (Player p : bar.getPlayers())
            bar.removePlayer(p);
    }

    public void clear() {
        for (Player player : players)
            bar.removePlayer(player);
    }

    public void setTitle(TextComponent title) {
        this.title = title;
        this.bar.setTitle(title.content());
    }

    public void setRunning(boolean enabled) {

        this.running = enabled;
    }

    public int getTimeLeft() {
        return time;
    }

    public void setTimeLeft(int timeLeft) {
        this.time = timeLeft;
    }

    public void reduceTime() {
            this.time--;
    }

    public void removePlayer(Player player) {
        if (bar.getPlayers().contains(player)) return;
        bar.addPlayer(player);
    }

    public boolean isRunning() {
        return running;
    }

    public BossBar getBossBar() {
        return bar;
    }

    public BarFlag getBarFlag() {
        return BarFlag.valueOf("");
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }
}

