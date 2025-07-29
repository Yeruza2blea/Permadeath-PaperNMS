package dev.yeruza.plugin.permadeath.plugin.block;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;

public class BlockProperties {
    public static final NamespacedKey BLOCK_ID = Permadeath.withCustomNamespace("nms_block");

    protected BlockData data;
    protected final CreatureSpawner meta;

    public BlockProperties(NamespacedKey id, Material material) {
        data = material.createBlockData();

        meta = (CreatureSpawner) data.createBlockState();
        meta.getPersistentDataContainer().set(BLOCK_ID, PersistentDataType.STRING, id.getKey());
    }

    public BlockProperties setId(String value) {
        meta.getPersistentDataContainer().set(BLOCK_ID, PersistentDataType.STRING, value);

        return this;
    }

    public BlockProperties createSound(SoundGroup sound) {

        return this;
    }


    public BlockState build() {
        return data.createBlockState();
    }

    static class NmsBlockData implements PersistentDataType<String, String> {
        String valueId;
        boolean canDrop;



        @NotNull
        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @NotNull
        @Override
        public Class<String> getComplexType() {
            return String.class;
        }

        @NotNull
        @Override
        public String toPrimitive(@NotNull String complex, @NotNull PersistentDataAdapterContext context) {
            return "";
        }

        @NotNull
        @Override
        public String fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
            return "";
        }
    }
}
