package dev.yeruza.plugin.permadeath.plugin.item.utils.base;

import org.bukkit.inventory.ItemStack;

public interface Kits<E extends ComponentKit> {
    ItemStack applyData(E type);
}
