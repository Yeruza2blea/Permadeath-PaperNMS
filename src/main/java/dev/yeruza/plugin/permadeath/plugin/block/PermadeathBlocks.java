package dev.yeruza.plugin.permadeath.plugin.block;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.core.CoreRegistries;
import dev.yeruza.plugin.permadeath.core.CoreRegistry;

import java.util.function.BiFunction;

public final class PermadeathBlocks {
    public static final BlockState INFERNAL_NETHERITE_BLOCK = registerBlock("infernal_netherite");
    public static final BlockState ALMORITY_BLOCK = registerBlock("almority_block");
    public static final BlockState CORE_POWER = registerBlock("core_power", (n, m) -> new BlockProperties(n, m));

    public static BlockState registerBlock(String id) {
        return registerBlock(id, BlockProperties::new);
    }

    public static BlockState registerBlock(String id, BiFunction<NamespacedKey, Material, BlockProperties> fabric) {
        return registerBlock(Permadeath.withCustomNamespace(id), fabric, Material.SPAWNER);
    }

    public static BlockState registerBlock(String id, BiFunction<NamespacedKey, Material, BlockProperties> fabric, Material material) {
        return registerBlock(Permadeath.withCustomNamespace(id), fabric, material);
    }

    public static BlockState registerBlock(NamespacedKey id, BiFunction<NamespacedKey, Material, BlockProperties> fabric, Material material) {
        BlockProperties properties = fabric.apply(id, material);
        properties.setId(id.getKey());

        return CoreRegistry.register(CoreRegistries.BLOCKS, id, properties.build());
    }

}
