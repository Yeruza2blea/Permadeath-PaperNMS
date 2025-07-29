package dev.yeruza.plugin.permadeath.plugin.item.utils.base;

import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;

import java.util.List;

public class BannerProperties extends ItemProperties {
    public BannerProperties(Material material, List<Pattern> patterns) {
        super(material);

        if (meta instanceof BannerMeta banner) {
            banner.setPatterns(patterns);
        }

    }
}
