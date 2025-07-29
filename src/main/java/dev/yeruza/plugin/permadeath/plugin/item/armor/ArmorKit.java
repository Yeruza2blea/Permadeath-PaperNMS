package dev.yeruza.plugin.permadeath.plugin.item.armor;

import io.papermc.paper.registry.TypedKey;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlot;
import dev.yeruza.plugin.permadeath.plugin.AttributeModifier;
import dev.yeruza.plugin.permadeath.plugin.item.utils.components.Equippable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public record ArmorKit(int durability, Map<ArmorType, Integer> defense, float knockbackResistant, float toughness, int enchantmentValue, TypedKey<Sound> equipSound, Optional<Color> color, NamespacedKey assetId) {
    public static final int UNIT_DURABILITY = 16;

    public static final Map<ArmorType, Integer> DEFENSER = new EnumMap<>(Map.of(
            ArmorType.HELMET, 3,
            ArmorType.CHESTPLATE, 8,
            ArmorType.LEGGINGS, 6,
            ArmorType.BOOTS, 3)
    );


    public void createAttributes(ArmorType type, ArmorProperties properties, String name) {
        Integer armor = defense.getOrDefault(type, 0);
        EquipmentSlot slot = type.getArmorSlot();

        properties.setName(name);

        properties.addAttributeModifier(
                new AttributeModifier(Attribute.ARMOR, slot, (double) armor),
                new AttributeModifier(Attribute.ARMOR_TOUGHNESS, slot, toughness),
                new AttributeModifier(Attribute.KNOCKBACK_RESISTANCE, slot, knockbackResistant)
        ).setEquippable(
                new Equippable(slot, equipSound, assetId)
        ).setDamageableMax(UNIT_DURABILITY * durability).addEnchamentable(enchantmentValue);
    }
}
