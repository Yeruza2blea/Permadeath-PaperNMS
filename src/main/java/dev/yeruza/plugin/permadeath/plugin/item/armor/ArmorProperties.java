package dev.yeruza.plugin.permadeath.plugin.item.armor;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;

import java.util.stream.Stream;

public class ArmorProperties extends ItemProperties {
    public static ItemStack[] pieces(Material helmet, Material chestplate, Material leggings, Material boots) {
        return Stream.of(helmet, chestplate, leggings, boots).map(ItemStack::new).toArray(ItemStack[]::new);
    }

    public static ItemStack[] pieces(ItemProperties helmet, ItemProperties chestplate, ItemProperties leggings, ItemProperties boots) {
        return Stream.of(helmet, chestplate, leggings, boots).map(ItemProperties::build).toArray(ItemStack[]::new);
    }

    public ArmorProperties(ArmorKit kit, ArmorType type, String name) {
        super(type.getMaterial());

        kit.createAttributes(type, this, name);
    }

    public ArmorProperties(Material material, ArmorTrim trim) {
        super(material);

        if (meta instanceof ArmorMeta armor) {
            armor.setTrim(trim);
            armor.setUnbreakable(true);
        }
    }

    public ArmorProperties(Material material, Color color) {
        super(material);


        meta.setUnbreakable(true);
        meta.getEquippable().setModel(Permadeath.withCustomNamespace("custom"));
    }
}
