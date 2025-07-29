package dev.yeruza.plugin.permadeath.gaming;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Menu  {
    void onClick(Player player);

    void open(Player player);

    void onClose(Player player);

    List<ItemStack> getSlots();
}
