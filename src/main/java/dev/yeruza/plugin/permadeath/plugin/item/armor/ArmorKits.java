package dev.yeruza.plugin.permadeath.plugin.item.armor;

import io.papermc.paper.registry.keys.SoundEventKeys;
import org.bukkit.Color;
import org.bukkit.Sound;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.Optional;

public interface ArmorKits {
    ArmorKit NETHERITE_ORIGINAL = new ArmorKit(
            41, ArmorKit.DEFENSER,
            0.1F,
            3,
            16,
            SoundEventKeys.ITEM_ARMOR_EQUIP_NETHERITE,
            Optional.of(Color.BLACK),
            Permadeath.withCustomNamespace("netherite_original")
    );
    ArmorKit INFERNAL_NETHERITE_ORIGINAL = new ArmorKit(
            44, ArmorKit.DEFENSER,
            0.1F,
            3,
            16,
            SoundEventKeys.ITEM_ARMOR_EQUIP_NETHERITE,
            Optional.of(Color.RED),
            Permadeath.withCustomNamespace("infernal_netherite_original")
    );

    ArmorKit PURE_NETHERITE = new ArmorKit(
            45,
            ArmorKit.DEFENSER,
            0.2F,
            4,
            17,
            SoundEventKeys.ITEM_ARMOR_EQUIP_NETHERITE,
            Optional.empty(),
            Permadeath.withCustomNamespace("pure_netherite")
    );

    ArmorKit INFERNAL_NETHERITE = new ArmorKit(
            54,
            ArmorKit.DEFENSER,
            0.3F,
            4,
            17,
            SoundEventKeys.ITEM_ARMOR_EQUIP_NETHERITE,
            Optional.empty(),
            Permadeath.withCustomNamespace("infernal_netherite")
    );

    ArmorKit ALMORITY = new ArmorKit(
            63,
            ArmorKit.DEFENSER, 0.5F,
            5, 25,
            SoundEventKeys.ITEM_ARMOR_EQUIP_NETHERITE,
            Optional.empty(),
            Permadeath.withCustomNamespace("almority")
    );
}
