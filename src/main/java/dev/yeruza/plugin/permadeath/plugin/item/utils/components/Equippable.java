package dev.yeruza.plugin.permadeath.plugin.item.utils.components;

import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.Set;

public record Equippable(EquipmentSlot slot, TypedKey<Sound> equipSound, NamespacedKey model, NamespacedKey camera, RegistryKeySet<EntityType> entities, boolean dispensable, boolean swappable, boolean damageOnHurt, boolean equipOnInteract) {
    public Equippable(EquipmentSlot slot) {
        this(slot, null, null, null, null, false, false, false, false);
    }

    public Equippable(EquipmentSlot slot, TypedKey<Sound> equipSound, NamespacedKey model) {
        this(slot, equipSound, model, null, null, false, false, false, false);
    }

    public Equippable(EquipmentSlot slot, TypedKey<Sound> equipSound, String assetId) {
        this(slot, equipSound, Permadeath.withCustomNamespace(assetId), null, null, false, false, false, false);
    }

    public Equippable addEquipSound(TypedKey<Sound> equipSound) {
        return new Equippable(slot, equipSound, model, camera, entities, dispensable, swappable, damageOnHurt, equipOnInteract);
    }

    public Equippable addModel(NamespacedKey model) {
        return new Equippable(slot, equipSound, model, camera, entities, dispensable, swappable, damageOnHurt, equipOnInteract);
    }

    public Equippable addCameraOverlay(NamespacedKey camera) {
        return new Equippable(slot, equipSound, model, camera, entities, dispensable, swappable, damageOnHurt, equipOnInteract);
    }

    public Equippable addAllowedEntities(RegistryKeySet<EntityType> entities) {
        return new Equippable(slot, equipSound, model, camera, entities, dispensable, swappable, damageOnHurt, equipOnInteract);
    }
}
