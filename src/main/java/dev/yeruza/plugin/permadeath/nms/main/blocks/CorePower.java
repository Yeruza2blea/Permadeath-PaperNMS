package dev.yeruza.plugin.permadeath.nms.main.blocks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import dev.yeruza.plugin.permadeath.nms.main.NmsBlock;

public class CorePower extends NmsBlock {
    public CorePower() {
        super("core", new ItemStack(Material.DIAMOND));
    }
}
