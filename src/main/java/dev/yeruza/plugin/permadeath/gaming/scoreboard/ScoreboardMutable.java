package dev.yeruza.plugin.permadeath.gaming.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.*;

public class ScoreboardMutable {
    public static Scoreboard createNewScoreboard() {
        return Bukkit.getScoreboardManager().getNewScoreboard();
    }

    private final NamespacedKey scoreId;

    private String title;
    private Map<String, Integer> lines;

    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    private final List<UUID> playersId = new ArrayList<>();

    public ScoreboardMutable(String id, String title, List<String> arrLines) {
        this(id, title, new TreeMap<>());

        List<String> reversed = arrLines.reversed();

        for (int i = 0; i <= arrLines.size(); ++i) {
            lines.put(reversed.get(i), i);
        }
    }

    public ScoreboardMutable(String id, String title, Map<String, Integer> lines) {
        this.scoreId = Permadeath.withCustomNamespace(id);
        this.title = title;
        this.lines = lines;

        Objective objective = scoreboard.registerNewObjective(id, Criteria.DUMMY, TextFormat.write(title));

        for (Map.Entry<String, Integer> score : lines.entrySet()) {
            objective.getScore(score.getKey()).setScore(score.getValue());
        }

    }

    public void addPlayer(Player player) {
        playersId.add(player.getUniqueId());
        player.setScoreboard(scoreboard);
    }

    public void removePlayer(Player player) {
        playersId.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public NamespacedKey getScoreId() {
        return scoreId;
    }
}
