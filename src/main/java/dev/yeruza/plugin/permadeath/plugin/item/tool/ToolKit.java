package dev.yeruza.plugin.permadeath.plugin.item.tool;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.ToolComponent;
import dev.yeruza.plugin.permadeath.plugin.AttributeModifier;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;

import java.util.List;

public record ToolKit(int durability, float speed, float attackDamage, int enchantmentValue) {
    public void createCommonAttributes(ItemProperties properties) {
        properties.setUnbreakable()
                .setFireResistant()
                .setDamageableMax(durability)
                .addEnchamentable(enchantmentValue);
    }

    public void createToolAttributes(ToolProperties properties, String name, float attackDamage, float attackSpeed, float blockingForSeconds) {
        createCommonAttributes(properties.setName(name));

        ItemStack stack = properties.build();
        ItemMeta meta = stack.getItemMeta();

        List<ToolComponent.ToolRule> rules = List.of(
                meta.getTool().addRule(Tag.INCORRECT_FOR_NETHERITE_TOOL, 0.0F, false),
                meta.getTool().addRule(Tag.INCORRECT_FOR_NETHERITE_TOOL, speed, true)
        );

        properties.setToolComponent(rules, 1.0F, 1, true)
                .addAttributeModifier(attributesTool(attackDamage, attackSpeed))
                .setWeapon(1, blockingForSeconds);
    }

    public void createSwordAttributes(ToolProperties properties, String name, float attackDamage, float attackSpeed) {
        createCommonAttributes(properties.setName(name));
        ItemStack stack = properties.build();
        ItemMeta meta = stack.getItemMeta();

        List<ToolComponent.ToolRule> rules = List.of(
                meta.getTool().addRule(Material.COBWEB, 15F, true),
                meta.getTool().addRule(Tag.INCORRECT_FOR_NETHERITE_TOOL, Float.MAX_VALUE, false),
                meta.getTool().addRule(Tag.SWORD_EFFICIENT, 1.5F, true)
        );

        properties.setToolComponent(rules, 1.0F, 2, false)
                .addAttributeModifier(attributesSword(attackDamage, attackSpeed))
                .setWeapon(1, 0.0F);
    }

    private AttributeModifier[] attributesSword(float attackDamage, float attackSpeed) {
        return List.of(
                new AttributeModifier(AttributeModifier.BASE_ATTACK_SPEED, EquipmentSlot.HAND, attackSpeed),
                new AttributeModifier(AttributeModifier.BASE_ATTACK_DAMAGE, EquipmentSlot.HAND, this.attackDamage + attackDamage)
        ).toArray(AttributeModifier[]::new);
    }

    private AttributeModifier[] attributesTool(float attackDamage, float attackSpeed) {
        return List.of(
                new AttributeModifier(AttributeModifier.BASE_ATTACK_SPEED, EquipmentSlot.HAND, attackSpeed),
                new AttributeModifier(AttributeModifier.BASE_ATTACK_DAMAGE, EquipmentSlot.HAND, this.attackDamage + attackDamage)
        ).toArray(AttributeModifier[]::new);
    }
}
