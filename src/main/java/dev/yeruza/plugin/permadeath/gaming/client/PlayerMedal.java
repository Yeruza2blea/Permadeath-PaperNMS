package dev.yeruza.plugin.permadeath.gaming.client;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.NamespacedKey;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.List;

public record PlayerMedal(NamespacedKey medalId, String name, CharSequence code, List<String> lore) {
    public PlayerMedal(String medalId, String name, CharSequence code, List<String> lore) {
        this(Permadeath.withCustomNamespace(medalId), name, code, lore);
    }

    @Override
    public String name() {
        return TextFormat.write(name).content();
    }

    @Override
    public List<String> lore() {
        return TextFormat.write(lore)
            .stream()
            .map(TextComponent::content)
            .toList();
    }

}
