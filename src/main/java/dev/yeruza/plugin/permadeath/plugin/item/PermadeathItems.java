package dev.yeruza.plugin.permadeath.plugin.item;

import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.keys.SoundEventKeys;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.AttributeModifier;
import dev.yeruza.plugin.permadeath.plugin.block.PermadeathBlocks;
import dev.yeruza.plugin.permadeath.core.CoreRegistries;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorKits;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorProperties;
import dev.yeruza.plugin.permadeath.plugin.item.armor.ArmorType;
import dev.yeruza.plugin.permadeath.plugin.item.tool.ToolKits;
import dev.yeruza.plugin.permadeath.plugin.item.tool.ToolProperties;
import dev.yeruza.plugin.permadeath.plugin.item.tool.ToolType;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class PermadeathItems {
    public static final ItemStack BOW_DEFINITIVE = registerItemCustom("bow_definitive", (m, n) -> new ItemProperties(m, n)
            .setName("&#E64B43A&#E64B43r&#D14854c&#BC4664o &#914086d&#7C3E96e &#562E8Fl&#442278a &#5C2670m&#85377Fu&#AE478Fe&#D7589Er&#D7589Et&#D7589Ee")
            .addEnchant(Enchantment.POWER, 32765)
            .setUnbreakable(), Material.BOW);

    public static final ItemStack SURVIVOR_MEDAL = registerItemCustom("survivor_medal", (m, n) -> new ItemProperties(m, n)
            .setName("&4&l[&c&l☠&4&l] &c&k| &r&6&lMedalla de Superviviente &c&k| &r&4&l[&c&l☠&4&l]"), Material.TOTEM_OF_UNDYING);

    public static final ItemStack ORB_LIFE = registerItemCustom("orb_life", (m, n) -> new ItemProperties(m, n)
            .setName("&6Orbe de vida")
            .setFireResistant()
            .setMaxStackOneSize(), Material.BROWN_DYE);

    public static final ItemStack ORB_DEATH = registerItemCustom("orb_death", (m, n) -> new ItemProperties(m, n)
            .setName("&6Orbe de muerte")
            .enableEnchantmentGlintOverride()
            .setMaxStackOneSize(), Material.BLACK_DYE);
    public static final ItemStack END_RELIC = registerItemCustom("end_relic", (m, n) -> new ItemProperties(m, n)
            .setName("&#59e2f8End Relic")
            .setLore("&6La reliquía te devuelve los 5 espacios del inventario que fueron bloqueados")
            .setFireResistant()
            .setMaxStackOneSize(), Material.LIGHT_BLUE_DYE);
    public static final ItemStack BEGINNING_RELIC = registerItemCustom("beginning_relic",  (m, n) -> new ItemProperties(m, n)
            .setName("&#2092a4Beginning Relic")
            .setLore("&6La reliquía te devuelve la mayoría de espacios del inventario que fueron bloqueados")
            .setFireResistant(), Material.CYAN_DYE);

    public static final ItemStack INFINITE_RELIC = registerItemCustom("infinite_relic", (m, n) -> new ItemProperties(m, n)
            .setName("&#b1eaffInfinite Relic")
            .setLore("&6La reliquía te devuelve los espacios de armadura que fueron bloqueados")
            .setFireResistant(), Material.PURPLE_DYE);
    public static final ItemStack SUPER_GOLDEN_APPLE_PLUS = registerItemCustom("super_golden_apple_plus", (m, n) -> new ItemProperties(m, n)
            .setName("&eSuper Golden Apple &b+")
            .enableEnchantmentGlintOverride(), Material.GOLDEN_APPLE);
    public static final ItemStack HYPER_GOLDEN_APPLE_PLUS = registerItemCustom("hyper_golden_apple_plus", (m, n) -> new ItemProperties(m, n)
            .setName("&6Hyper Golden Apple &b+")
            .enableEnchantmentGlintOverride()
            .setMaxStackOneSize(), Material.GOLDEN_APPLE);
    public static final ItemStack ULTRA_GOLDEN_APPLE_PLUS = registerItemCustom("ultra_golden_apple_plus",  (m, n) -> new ItemProperties(m, n)
            .setName("&5Ultra Golden Apple &6+")
            .enableEnchantmentGlintOverride()
            .setMaxStackSize(2), Material.GOLDEN_APPLE);
    public static final ItemStack INFERNAL_NETHERITE_ELYTRA = registerItemCustom("infernal_netherite_elytra", (m, n) -> new ItemProperties(m, n)
            .setName("&cInfernal Netherite Elytra")
            .setEquippable(EquipmentSlot.CHEST, SoundEventKeys.ITEM_ARMOR_EQUIP_NETHERITE, Permadeath.withCustomNamespace("infernal_netherite_elytra"))
            .addAttributeModifier(
                    new AttributeModifier(Attribute.ARMOR, EquipmentSlot.BODY, 8),
                    new AttributeModifier(Attribute.ARMOR_TOUGHNESS, EquipmentSlot.BODY, 3),
                    new AttributeModifier(Attribute.KNOCKBACK_RESISTANCE, EquipmentSlot.BODY, 0.2F)
            )
            .setFireResistant(), Material.ELYTRA);
    public static final ItemStack PURE_NETHERITE_INGOT = registerItemCustom("pure_netherite_ingot", (n, m) -> new ItemProperties(n, m).setName("&5Pure Netherite Ingot"), Material.NETHERITE_INGOT);
    public static final ItemStack INFERNAL_NETHERITE_BLOCK = registerBlockCustom((CreatureSpawner) PermadeathBlocks.INFERNAL_NETHERITE_BLOCK, () -> new BlockItemProperties(Material.NETHERITE_INGOT, "&cInfernal Netherite Block"));
    public static final ItemStack ALMORITY_BLOCK = registerBlockCustom((CreatureSpawner) PermadeathBlocks.ALMORITY_BLOCK, () -> new BlockItemProperties(Material.NETHERITE_INGOT,"&6Almority Block"));

    public static final ItemStack PURE_NETHERITE_HELMET = registerArmorCustom("pure_netherite_helmet", () -> new ArmorProperties(ArmorKits.PURE_NETHERITE, ArmorType.HELMET, "&5Pure Netherite Helmet"));
    public static final ItemStack PURE_NETHERITE_CHESTPLATE =  registerArmorCustom("pure_netherite", () -> new ArmorProperties(ArmorKits.PURE_NETHERITE, ArmorType.CHESTPLATE, "&5Pure Netherite Chestplate"));
    public static final ItemStack PURE_NETHERITE_LEGGINGS = registerArmorCustom("pure_netherite_leggings", () -> new ArmorProperties(ArmorKits.PURE_NETHERITE, ArmorType.LEGGINGS, "&5Pure Netherite Leggings"));
    public static final ItemStack PURE_NETHERITE_BOOTS = registerArmorCustom("pure_netherite_boots", () -> new ArmorProperties(ArmorKits.PURE_NETHERITE, ArmorType.BOOTS, "&5Pure Netherite Boots"));

    public static final ItemStack INFERNAL_NETHERITE_HELMET = registerArmorCustom("infernal_netherite_helmet", () -> new ArmorProperties(ArmorKits.INFERNAL_NETHERITE, ArmorType.HELMET, "&cInfernal Netherite Helmet"));
    public static final ItemStack INFERNAL_NETHERITE_CHESTPLATE = registerArmorCustom("infernal_netherite_chestplate", () -> new ArmorProperties(ArmorKits.INFERNAL_NETHERITE, ArmorType.CHESTPLATE, "&cInfernal Netherite Chestplate"));
    public static final ItemStack INFERNAL_NETHERITE_LEGGINGS =  registerArmorCustom("infernal_netherite_leggings", () -> new ArmorProperties(ArmorKits.INFERNAL_NETHERITE, ArmorType.LEGGINGS, "&cInfernal Netherite Leggings"));
    public static final ItemStack INFERNAL_NETHERITE_BOOTS = registerArmorCustom("infernal_netherite_boots", () -> new ArmorProperties(ArmorKits.INFERNAL_NETHERITE, ArmorType.BOOTS, "&cInfernal Netherite Boots"));

    public static final ItemStack ALMORITY_HELMET = registerArmorCustom("almority_helmet", () -> new ArmorProperties(ArmorKits.ALMORITY, ArmorType.HELMET, "&6Almority Helmet"));
    public static final ItemStack ALMORITY_CHESTPLATE = registerArmorCustom("almority_chestplate", () -> new ArmorProperties(ArmorKits.ALMORITY, ArmorType.CHESTPLATE, "&6Almority Chestplate"));
    public static final ItemStack ALMORITY_LEGGINGS = registerArmorCustom("almority_leggings", () -> new ArmorProperties(ArmorKits.ALMORITY, ArmorType.LEGGINGS, "&6Almority Leggings"));
    public static final ItemStack ALMORITY_BOOTS = registerArmorCustom("almority_boots", () -> new ArmorProperties(ArmorKits.ALMORITY, ArmorType.BOOTS, "&6Almority Boots"));

    public static final ItemStack PURE_NETHERITE_SWORD = registerToolCustom("pure_netherite_sword", () -> new ToolProperties(ToolKits.PURE_NETHERITE, ToolType.SWORD, "&5Pure Netherite Sword",4.0F, -2.4F));
    public static final ItemStack PURE_NETHERITE_PICKAXE = registerToolCustom("pure_netherite_pickaxe", () -> new ToolProperties(ToolKits.PURE_NETHERITE, ToolType.PICKAXE, "&5Pure Netherite Pickaxe",2.0F, -2.8F));
    public static final ItemStack PURE_NETHERITE_AXE = registerToolCustom("pure_netherite_axe", () -> new ToolProperties(ToolKits.PURE_NETHERITE, ToolType.AXE, "&5Pure Netherite Axe",6.0F, -3.0F));
    public static final ItemStack PURE_NETHERITE_SHOVEL = registerToolCustom("pure_netherite_shovel", () -> new ToolProperties(ToolKits.PURE_NETHERITE, ToolType.SHOVEL, "&5Pure Netherite Shovel",2.5F, -3.0F));
    public static final ItemStack PURE_NETHERITE_HOE = registerToolCustom("pure_netherite_hoe", () -> new ToolProperties(ToolKits.PURE_NETHERITE, ToolType.HOE, "&5Pure Netherite Hoe",-3.0F, 0.0F));

    public static final ItemStack INFERNAL_NETHERITE_SWORD = registerToolCustom("infernal_netherite_sword", () -> new ToolProperties(ToolKits.INFERNAL_NETHERITE, ToolType.SWORD, "&cInfernal Netherite Sword", 5.0F, -2.4F));
    public static final ItemStack INFERNAL_NETHERITE_PICKAXE = registerToolCustom("infernal_netherite_pickaxe", () -> new ToolProperties(ToolKits.INFERNAL_NETHERITE, ToolType.PICKAXE, "&cInfernal Netherite Pickaxe", 2.0F, -2.8F));
    public static final ItemStack INFERNAL_NETHERITE_AXE = registerToolCustom("infernal_netherite_axe", () -> new ToolProperties(ToolKits.INFERNAL_NETHERITE, ToolType.AXE, "&cInfernal Netherite Axe", 5.0F, -3.0F));
    public static final ItemStack INFERNAL_NETHERITE_SHOVEL = registerToolCustom("infernal_netherite_shovel", () ->  new ToolProperties(ToolKits.INFERNAL_NETHERITE, ToolType.SHOVEL, "&cInfernal Netherite Shovel", 3.5F, -2.0F));
    public static final ItemStack INFERNAL_NETHERITE_HOE = registerToolCustom("infernal_netherite_hoe", () -> new ToolProperties(ToolKits.INFERNAL_NETHERITE, ToolType.HOE, "&cInfernal Netherite Hoe", -3.0F, 0.0F));

    public static final ItemStack ALMORITY_SWORD = registerToolCustom("almority_sword", () -> new ToolProperties(ToolKits.ALMORITY, ToolType.SWORD, "&6Almority Sword", 6.0F, -2.4F));
    public static final ItemStack ALMORITY_PICKAXE = registerToolCustom("almority_pickaxe",() -> new ToolProperties(ToolKits.ALMORITY, ToolType.PICKAXE, "&6Almority Pickaxe", 2.0F, 2.8F));
    public static final ItemStack ALMORITY_AXE = registerToolCustom("almority_axe", () -> new ToolProperties(ToolKits.ALMORITY, ToolType.AXE, "&6Almority Axe", 5.0F, -3.0F));
    public static final ItemStack ALMORITY_SHOVEL = registerToolCustom("almority_shovel", () -> new ToolProperties(ToolKits.ALMORITY, ToolType.SHOVEL, "&6Almority Shovel", 3.5F, -2.0F));
    public static final ItemStack ALMORITY_HOE = registerToolCustom("almority_hoe", () -> new ToolProperties(ToolKits.ALMORITY, ToolType.HOE, "&6Almority Hoe", -3.0F, 0.0F));

    public static final ItemStack WITCH_ROD = registerItemCustom("witch_rod", (m, n) -> new ItemProperties(m, n)
            .setName("&#744ad4Witch Rod")
            .setConsumable(Long.MAX_VALUE, ItemUseAnimation.SPEAR, false)
            .addAttributeModifier(
                    new AttributeModifier(AttributeModifier.BASE_ATTACK_DAMAGE, EquipmentSlot.HAND, 12.5D),
                    new AttributeModifier(AttributeModifier.BASE_ATTACK_SPEED, EquipmentSlot.HAND, -2.0D)
            ), Material.BLAZE_ROD);

    public static final ItemStack AMON_ROD = registerItemCustom("amon_rod", (m, n) -> new ItemProperties(m, n)
            .setName("&4Amon Rod")
            .enableEnchantmentGlintOverride(), Material.BLAZE_ROD);


    @NotNull
    public static ItemStack registerItemCustom(String id, BiFunction<NamespacedKey, Material, ItemProperties> fabric, Material material) {
        return registerItemCustom(Permadeath.withCustomNamespace(id), fabric, material);
    }

    @NotNull
    public static ItemStack registerItemCustom(NamespacedKey id, BiFunction<NamespacedKey, Material, ItemProperties> fabric, Material material) {
        //ItemProperties properties = fabric.apply(id, material);

        String key = id.getKey();

        return fabric.apply(id, material)
                .setId(key)
                .setCustomModelData(String.class, List.of(key))
                .build();

    }

    public static ItemStack registerArmorCustom(String id, Supplier<ArmorProperties> fabric) {
        return registerArmorCustom(Permadeath.withCustomNamespace(id), fabric);
    }


    public static ItemStack registerArmorCustom(NamespacedKey id, Supplier<ArmorProperties> fabric) {
        ArmorProperties properties = fabric.get();

        properties.setId(id.getKey());
        properties.setCustomModelData(String.class, List.of(id.getKey()));

        return properties.build();
    }


    public static ItemStack registerBlockCustom(CreatureSpawner state, Supplier<BlockItemProperties> fabric) {
        ItemProperties properties = fabric.get();

        return properties.build();
    }


    public static ItemStack registerToolCustom(NamespacedKey id, Supplier<ToolProperties> fabric) {
        ToolProperties tool = fabric.get();

        tool.setId(id.getKey());
        tool.setCustomModelData(String.class, List.of(id.getKey()));

        return tool.build();
    }

    public static ItemStack registerToolCustom(String id, Supplier<ToolProperties> fabric) {
        return registerToolCustom(Permadeath.withCustomNamespace(id), fabric);
    }

    public static Set<ItemStack> values() {
        return CoreRegistries.ITEM.values();

    }
}

