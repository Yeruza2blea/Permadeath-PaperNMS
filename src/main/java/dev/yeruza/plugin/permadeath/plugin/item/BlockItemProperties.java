package dev.yeruza.plugin.permadeath.plugin.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

public class BlockItemProperties extends ItemProperties {
    public BlockItemProperties(NamespacedKey key, Material material) {
        super(key, material);
    }

    public BlockItemProperties(Material material, String name) {
        super(material);

        meta.displayName(TextFormat.write(name));
    }
}
