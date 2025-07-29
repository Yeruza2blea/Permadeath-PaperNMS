package dev.yeruza.plugin.permadeath.gaming.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

public class TeamMutable {
    public static Team createNewTeam(String id) {
        return Bukkit.getScoreboardManager().getNewScoreboard().registerNewTeam(id);
    }
}
