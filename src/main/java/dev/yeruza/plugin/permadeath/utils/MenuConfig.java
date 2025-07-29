package dev.yeruza.plugin.permadeath.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;

public class MenuConfig implements InventoryHolder {
    private final Inventory inventory;

    public MenuConfig(Permadeath plugin, int size) {
        this.inventory = plugin.getServer().createInventory(this, size);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
