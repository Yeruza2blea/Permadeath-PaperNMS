package dev.yeruza.plugin.permadeath.plugin.item.armor;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

public enum ArmorType {
    HELMET(EquipmentSlot.HEAD, Material.NETHERITE_HELMET, "helmet", 1),
    CHESTPLATE(EquipmentSlot.CHEST, Material.NETHERITE_CHESTPLATE, "chestplate", 2),
    LEGGINGS(EquipmentSlot.LEGS, Material.NETHERITE_LEGGINGS, "leggings", 3),
    BOOTS(EquipmentSlot.FEET, Material.NETHERITE_BOOTS, "boots", 4);


    private final Material type;
    private final String name;
    private final EquipmentSlot slot;
    private int pos;

    ArmorType(EquipmentSlot slot, Material type, String name, int pos) {
        this.slot = slot;
        this.type = type;
        this.name = name;
        this.pos = pos;
    }


    public String getId() {
        return name;
    }


    public Material getMaterial() {
        return type;
    }

    public EquipmentSlot getArmorSlot() {
        return slot;
    }

    public int getPos() {
        int order = ordinal();

        return pos - 1;
    }

    @Override
    public String toString() {
        return super.name().toLowerCase();
    }
}
