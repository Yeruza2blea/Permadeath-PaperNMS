package dev.yeruza.plugin.permadeath.plugin;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlot;

public record AttributeModifier(Attribute attribute, EquipmentSlot slot, Operator operation, double amount) implements BukkitBooster<org.bukkit.attribute.AttributeModifier> {
    public static final Attribute BASE_ATTACK_DAMAGE = getAttribute("base_attack_damage");
    public static final Attribute BASE_ATTACK_SPEED = getAttribute("base_attack_speed");

    private static Attribute getAttribute(String name) {

        return Registry.ATTRIBUTE.get(NamespacedKey.minecraft(name));
    }


    public AttributeModifier(Attribute attribute, EquipmentSlot slot, double amount) {
        this(attribute, slot, Operator.ADD_NUMBER, amount);
    }

    public org.bukkit.attribute.AttributeModifier getBukkit() {
        return new org.bukkit.attribute.AttributeModifier(attribute.getKey(), amount, operation.getOriginal(), slot.getGroup());
    }

    public enum Operator {
        ADD_NUMBER(org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER),
        ADD_SCALAR(org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR),
        MULTIPLY_SCALAR_1(org.bukkit.attribute.AttributeModifier.Operation.MULTIPLY_SCALAR_1);

        final org.bukkit.attribute.AttributeModifier.Operation operation;

        Operator(org.bukkit.attribute.AttributeModifier.Operation base) {
            this.operation = base;
        }

        org.bukkit.attribute.AttributeModifier.Operation getOriginal() {
            return operation;
        }
    }
}