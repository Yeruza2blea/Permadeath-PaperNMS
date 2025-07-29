package dev.yeruza.plugin.permadeath.gaming;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public final class PermadeathTabList implements Runnable {
    private List<String> header;
    private List<String> footer;


    public PermadeathTabList() {
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListName("");
            player.setPlayerListFooter("");
            player.setPlayerListHeaderFooter("", "");
        }
    }
}
