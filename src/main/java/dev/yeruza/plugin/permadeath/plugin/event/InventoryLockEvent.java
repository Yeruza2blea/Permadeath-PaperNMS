package dev.yeruza.plugin.permadeath.plugin.event;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.item.ItemProperties;

import java.util.List;

public class InventoryLockEvent extends InventoryEvent {
    private static final int[] slots = {40, 34, 33, 32, 30, 29, 28, 27, 26, 25, 24, 23, 21, 20, 19, 18, 17, 16, 15, 14, 12, 11, 10, 9, 8, 7 };

    public InventoryLockEvent(@NotNull InventoryView transaction) {
        super(transaction);
    }



    public static void slotBlock(Player player) {
        if (Permadeath.getPlugin().getDay() < 40) return;
        if (player.getGameMode() == GameMode.SPECTATOR || player.isDead() || !player.isOnline()) return;

        boolean hasEndRelic = false;
        boolean hasBeginningRelic = false;
        boolean hasInfiniteRelic = false;

        int[] endRelicLockedSlots = {};
        if (Permadeath.getPlugin().getDay() < 60)
            endRelicLockedSlots = new int[] {40, 13, 22, 31, 4};
        else if (Permadeath.getPlugin().getDay() > 60)
            endRelicLockedSlots = new int[] {13, 22, 31, 4};


        for (ItemStack contents : player.getInventory().getContents()) {
            if (!hasBeginningRelic && Permadeath.getPlugin().isBeginningRelic(contents)) {
                hasBeginningRelic = true;
                hasEndRelic = true;
            } else if (!hasEndRelic && Permadeath.getPlugin().isEndRelic(contents))
                hasEndRelic = true;
        }

        int slot;
        if (Permadeath.getPlugin().getDay() >= 40) {
            for (int i : endRelicLockedSlots) {
                slot = i;
                if (hasEndRelic)
                    unlockSlot(player, slot);
                else
                    lockSlot(player, slot);
            }
        }

        if (Permadeath.getPlugin().getDay() >= 60) {
            for (int j : slots) {
                slot = j;
                if (hasBeginningRelic)
                    unlockSlot(player, slot);
                else
                    lockSlot(player, slot);
            }
        }

        if (Permadeath.getPlugin().getDay() >= 80) {
            for (int l : slots) {
                slot = l;
                if (hasBeginningRelic)
                    unlockSlot(player, slot);
                else
                    lockSlot(player, slot);
            }
        }
    }

    private static void lockSlot(Player player, int slot) {
        ItemStack item = player.getInventory().getItem(slot);

        if (item != null) {

            if (item.getType() != Material.AIR && item.getType() != Material.STRUCTURE_VOID) {
                player.getWorld().dropItem(player.getLocation(), item.clone());
            }

            item.setType(Material.STRUCTURE_VOID);
            item.setAmount(1);
        } else {
            ItemStack slotRemoved = new ItemProperties(Material.STRUCTURE_VOID)
                    .setName("&#FF0000Slot Bloqueado")
                    .setCustomModelData(String.class, List.of("slot_blocked"))
                    .build();


            player.getInventory().setItem(slot, slotRemoved);
        }
    }

    private static void unlockSlot(Player p, int slot) {
        ItemStack item = p.getInventory().getItem(slot);

        if (item != null && item.getItemMeta().hasDisplayName() && item.getType() == Material.STRUCTURE_VOID) {
            p.getInventory().clear(slot);
        }
    }
}
