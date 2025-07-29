package dev.yeruza.plugin.permadeath.plugin.item;


import dev.yeruza.plugin.permadeath.plugin.item.utils.components.custom_model_data.CustomModelData;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.Weapon;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.DamageTypeRegistryEntry;
import io.papermc.paper.registry.keys.SoundEventKeys;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.tag.DamageTypeTags;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.AttributeModifier;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.List;
import java.util.Map;

public class ItemProperties {
    public static final NamespacedKey ITEM_ID = Permadeath.withCustomNamespace("nms_item");


    protected ItemStack stack;
    protected final ItemMeta meta;


    public ItemProperties(ItemStack item) {
        stack = new ItemStack(item);
        meta = stack.getItemMeta();
    }

    public ItemProperties(Material material) {
        stack = new ItemStack(material);
        meta = stack.getItemMeta();
    }

    public ItemProperties(Material material, int amount) {
        stack = new ItemStack(material, amount);
        meta = stack.getItemMeta();
    }

    public ItemProperties(NamespacedKey key, Material material) {
        this.stack = new ItemStack(material);
        this.meta  = stack.getItemMeta();
        this.meta.getPersistentDataContainer().set(ITEM_ID, PersistentDataType.STRING, key.getKey());
    }

    public ItemProperties setName(String name) {
        meta.displayName(TextFormat.write(name));

        return this;
    }

    public ItemProperties setLore(String ...lore) {
        meta.lore(TextFormat.withCodes(lore));

        return this;
    }

    public ItemProperties setLore(List<String> lore) {
        meta.lore(TextFormat.write(lore));

        return this;
    }

    public ItemProperties setRarity(ItemRarity rarity) {
        meta.setRarity(rarity);

        return this;
    }

    public ItemProperties setUnbreakable() {
        meta.setUnbreakable(true);

        return this;
    }

    public ItemProperties setColor(Color color) {
        if (meta instanceof ColorableArmorMeta armor) {
            armor.setColor(color);
        }

        return this;
    }


    public ItemProperties setItemModel(NamespacedKey key) {
        meta.setItemModel(key);

        return this;
    }

    public <P, C> ItemProperties setId(NamespacedKey key, PersistentDataType<P, C> type, C value) {
        meta.getPersistentDataContainer().set(key, type, value);
        return this;
    }

    public ItemProperties setId(String value) {
        meta.getPersistentDataContainer().set(ITEM_ID, PersistentDataType.STRING, value);

        return this;
    }

    public ItemProperties setId(String key, String value) {
        meta.getPersistentDataContainer().set(Permadeath.withCustomNamespace(key), PersistentDataType.STRING, value);

        return this;
    }



    public ItemProperties addEnchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);

        return this;
    }

    public ItemProperties enableEnchantmentGlintOverride() {
        meta.setEnchantmentGlintOverride(true);

        return this;
    }

    public ItemProperties setDamage(int damage) {
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(damage);

            return this;
        }

        return this;
    }

    public ItemProperties setDamage(int max, int damage) {
        if (meta instanceof Damageable damageable) {
            damageable.setMaxDamage(max);
            damageable.setDamage(damage);

            return this;
        }

        return this;
    }

    public ItemProperties setDamageableMax(int max) {
        if (meta instanceof Damageable damageable) {
            damageable.setMaxDamage(max);
            damageable.setDamage(0);

            return this;
        }

        return this;
    }

    public ItemProperties setRepairable(int cost) {
        if (meta instanceof Repairable repairable) {
            repairable.setRepairCost(cost);

            return this;
        }

        return this;
    }

    public ItemProperties addEnchants(Map<Enchantment, Integer> enchantments) {
        if (!enchantments.isEmpty())
            for (Enchantment ench : enchantments.keySet()) {
                meta.addEnchant(ench, enchantments.get(ench), true);
            }

        return this;
    }

    public ItemProperties addItemFlag(ItemFlag itemflag) {
        meta.addItemFlags(itemflag);

        return this;
    }

    public ItemProperties addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        meta.addAttributeModifier(attribute, modifier.getBukkit());

        return this;
    }

    public ItemProperties addAttributeModifier(AttributeModifier...modifiers) {
        for (AttributeModifier modifier : modifiers) {
            meta.addAttributeModifier(modifier.attribute(), modifier.getBukkit());
        }

        return this;
    }

    public ItemProperties addEnchamentable(int value) {
        meta.setEnchantable(value);

        return this;
    }

    public ItemProperties setToolComponent(List<ToolComponent.ToolRule> rules, float defaultSpeed, int damagePer, boolean canDestroy) {
        ToolComponent component = meta.getTool();

        component.setRules(rules);
        component.setDefaultMiningSpeed(defaultSpeed);
        component.setDamagePerBlock(damagePer);

        return this;
    }

    public ItemProperties setWeapon(int damagerPer, float blockingForSeconds) {
        Weapon.Builder component = Weapon.weapon();

        component.itemDamagePerAttack(damagerPer);
        component.disableBlockingForSeconds(blockingForSeconds);

        return this;
    }

    public ItemProperties setFireResistant() {
        stack.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));

        return this;
    }

    public ItemProperties setDamageResistant(TagKey<DamageType> type) {
        stack.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(type));

        return this;
    }

    public ItemProperties setMaxStackSize(int size) {
        stack.setData(DataComponentTypes.MAX_STACK_SIZE, size);

        return this;
    }

    public ItemProperties setMaxStackOneSize() {
        stack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);

        return this;
    }

    public ItemProperties setHead(PlayerProfile owner) {
        if (meta instanceof SkullMeta skull) {
            skull.setOwnerProfile(owner);
        }

        return this;
    }


    public ItemProperties setHead(String owner, NamespacedKey key) {
        if (meta instanceof SkullMeta skull) {
            skull.setOwnerProfile(Bukkit.createPlayerProfile(owner));
            skull.setNoteBlockSound(key);
        }

        return this;
    }

    public ItemProperties setFood(int nutrition, int saturation, boolean canAlawaysEat) {
         FoodComponent component = meta.getFood();

         component.setNutrition(nutrition);
         component.setSaturation(saturation);
         component.setCanAlwaysEat(canAlawaysEat);

         meta.setFood(component);

         return this;
    }

    public ItemProperties setFoodCanAlwaysEat(int nutrition, int saturation) {
        FoodComponent component = meta.getFood();

        component.setNutrition(nutrition);
        component.setSaturation(saturation);
        component.setCanAlwaysEat(true);

        meta.setFood(component);

        return this;
    }

    public ItemProperties setConsumable(float sec, ItemUseAnimation animated, TypedKey<Sound> eatSound, boolean particles) {
        Consumable.Builder component = Consumable.consumable();

        component.consumeSeconds(sec);
        component.animation(animated);
        component.sound(eatSound);
        component.hasConsumeParticles(particles);

        stack.setData(DataComponentTypes.CONSUMABLE, component.build());

        return this;
    }

    public ItemProperties setConsumable(float sec, ItemUseAnimation animated, boolean particles) {
        Consumable.Builder component = Consumable.consumable();

        component.consumeSeconds(sec);
        component.animation(animated);
        component.hasConsumeParticles(particles);
        component.sound(SoundEventKeys.ENTITY_GENERIC_EAT);

        stack.setData(DataComponentTypes.CONSUMABLE, component.build());

        return this;
    }

    public ItemProperties setConsumable(dev.yeruza.plugin.permadeath.plugin.item.utils.components.consumable.Consumable consumable) {
        Consumable.Builder component = Consumable.consumable();

        component.consumeSeconds(consumable.seconds());
        component.animation(consumable.animation().getBukkit());
        component.sound(consumable.eatSound());
        component.hasConsumeParticles(consumable.hasConsumeParticles());
        component.addEffects(consumable.effects());

        stack.setData(DataComponentTypes.CONSUMABLE, component.build());

        return this;
    }

    public ItemProperties setEquippable(EquipmentSlot slot, NamespacedKey model, TypedKey<Sound> sound) {
        Equippable.Builder component = Equippable.equippable(slot);

        component.equipSound(sound);
        component.assetId(model);

        stack.setData(DataComponentTypes.EQUIPPABLE, component.build());

        return this;
    }

    public ItemProperties setEquippable(EquipmentSlot slot, TypedKey<Sound> sound, NamespacedKey model) {
        Equippable.Builder component = Equippable.equippable(slot);

        component.equipSound(sound);
        component.assetId(model);

        stack.setData(DataComponentTypes.EQUIPPABLE, component.build());

        return this;
    }


    public ItemProperties setEquippable(dev.yeruza.plugin.permadeath.plugin.item.utils.components.Equippable equippable) {
        Equippable.Builder component = Equippable.equippable(equippable.slot());

        component.equipSound(equippable.equipSound());
        component.assetId(equippable.model());
        component.cameraOverlay(equippable.camera());
        component.allowedEntities(equippable.entities());
        component.dispensable(equippable.dispensable());
        component.swappable(equippable.swappable());
        component.damageOnHurt(equippable.damageOnHurt());
        component.equipOnInteract(equippable.equipOnInteract());

        stack.setData(DataComponentTypes.EQUIPPABLE, component.build());

        return this;
    }

    public ItemProperties setTrim(ArmorTrim trim) {
        if (meta instanceof ArmorMeta armor) {
            armor.setTrim(trim);
        }

        return this;
    }

    public ItemProperties setCustomModelData(dev.yeruza.plugin.permadeath.plugin.item.utils.components.custom_model_data.CustomModelData data) {
        CustomModelDataComponent component = meta.getCustomModelDataComponent();
        component.setStrings(data.strings());
        component.setFloats(data.floats());
        component.setFlags(data.flags());
        component.setColors(data.colors());

        meta.setCustomModelDataComponent(component);

        return this;
    }

    public ItemProperties setCustomModelData(Object ...models) {
        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        if (models == null) {
            Permadeath.getPlugin().getLogger().warning("Custom Model Data no puede tener valores nulos");
            return this;
        }

        if (models instanceof String[] strings) {
            component.setStrings(List.of(strings));
        }
        if (models instanceof Float[] floats) {
            component.setFloats(List.of(floats));
        }
        if (models instanceof Boolean[] flags) {
            component.setFlags(List.of(flags));
        }
        if (models instanceof Color[] colors) {
            component.setColors(List.of(colors));
        }

        meta.setCustomModelDataComponent(component);

        return this;
    }

    public ItemProperties setCustomModelData(Class<?> type, Object ...models) {
        CustomModelDataComponent component = meta.getCustomModelDataComponent();

        if (models == null) {
            Permadeath.getPlugin().getLogger().warning("Custom Model Data no puede tener valores nulos");
            return this;
        }

        if (type == String.class && models instanceof String[] strings) {
            component.setStrings(List.of(strings));
        }

        if (type == Float.class && models instanceof Float[] floats) {
            component.setFloats(List.of(floats));
        }
        if (type == Boolean.class && models instanceof Boolean[] booleans) {
            component.setFlags(List.of(booleans));
        }
        if (type == Color.class && models instanceof Color[] colors) {
            component.setColors(List.of(colors));
        }

        meta.setCustomModelDataComponent(component);

        return this;
    }

    public ItemStack build() {
        stack.setItemMeta(meta);

        return stack;
    }
}
