package dev.yeruza.plugin.permadeath.gaming.client;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.permissions.Permission;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.List;

public record PlayerRank(NamespacedKey rankId, char charCode, List<String> lore, List<Permission> permissions) {
    public PlayerRank(String rankId, char charCode, List<String> lore, List<Permission> permissions) {
        this(Permadeath.withCustomNamespace(rankId), charCode, lore, permissions);
    }

    @Override
    public List<String> lore() {
        return TextFormat.write(lore)
            .stream()
            .map(TextComponent::content)
            .toList();
    }
}
