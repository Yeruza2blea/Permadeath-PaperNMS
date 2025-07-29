package dev.yeruza.plugin.permadeath.plugin.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class PotionProperties extends ItemProperties {

    public PotionProperties(ItemStack item) {
        super(item);
    }

    public PotionProperties(Material material) {
        super(material);
    }

    public PotionProperties setBasePotionType(PotionType type) {
        if (meta instanceof PotionMeta potion) {
            potion.setBasePotionType(type);
        }

        return this;
    }

    public PotionProperties setPotionEffectOverwrited(PotionEffect effect) {
        if (meta instanceof PotionMeta potion) {
            potion.addCustomEffect(effect, true);
        }

        return this;
    }

    public PotionProperties setPotionEffect(PotionEffect effect) {
        if (meta instanceof PotionMeta potion) {
            potion.addCustomEffect(effect, false);
        }

        return this;
    }
}
