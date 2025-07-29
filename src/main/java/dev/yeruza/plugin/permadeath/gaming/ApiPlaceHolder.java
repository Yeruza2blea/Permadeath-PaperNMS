package dev.yeruza.plugin.permadeath.gaming;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ApiPlaceHolder extends PlaceholderExpansion {
    @NotNull
    @Override
    public String getIdentifier() {
        return "permadeath";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "yeruza.elfather";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.21.4";
    }

    @Override
    @Nullable
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player.isOnline() && player instanceof Player p) {
            if (!params.startsWith("{") || !params.endsWith("}")) return null;
        }


        return null;
    }

}
