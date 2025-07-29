package dev.yeruza.plugin.permadeath.core;

import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class CoreRegistries {
    public static final CoreRegistry<ItemStack> ITEM = CoreRegistry.create("item");
    public static final CoreRegistry<BlockState> BLOCKS = CoreRegistry.create("block");
    public static final CoreRegistry<Enchantment> ENCHANTMENT = CoreRegistry.create("enchantment");
}
