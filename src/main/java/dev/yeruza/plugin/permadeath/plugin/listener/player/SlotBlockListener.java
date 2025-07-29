package dev.yeruza.plugin.permadeath.plugin.listener.player;

import org.bukkit.Material;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import dev.yeruza.plugin.permadeath.Permadeath;

import java.util.Random;

public class SlotBlockListener implements Listener {
    private Permadeath plugin;

    public SlotBlockListener(Permadeath plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!event.isCancelled()) return;
        if (event.getItemDrop().getItemStack().getType() == Material.STRUCTURE_VOID)
            event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClickVoid(InventoryClickEvent event) {
        if (!event.isCancelled()) return;
        if (event.getCurrentItem() != null) {
            if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                event.setCancelled(true);
                if (event.getClick() == ClickType.NUMBER_KEY) event.getInventory().remove(Material.STRUCTURE_VOID);
            }
        }

        if (event.getCursor() != null)
            if (event.getCursor().getType() == Material.STRUCTURE_VOID) event.setCancelled(true);


    }

    @EventHandler
    public void onItemCraft(PrepareItemCraftEvent event) {
        if (event.getInventory() != null) {
            if (event.getInventory().getResult() != null) {
                if (event.getInventory().getResult().getType() == Material.TORCH || event.getInventory().getResult().getType() == Material.REDSTONE_TORCH || event.getInventory().getResult().getType() == Material.BLAZE_ROD)
                    event.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.STRUCTURE_VOID)
            event.setCancelled(true);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) return;
        if (event.getOffHandItem() != null)
            if (event.getOffHandItem().getType() == Material.STRUCTURE_VOID)
                event.setCancelled(true);

        if (event.getMainHandItem() != null)
            if (event.getMainHandItem().getType() == Material.STRUCTURE_VOID)
                event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMoveItem(InventoryMoveItemEvent event) {

        if (event.isCancelled()) return;

        if (event.getItem() != null)

            if (event.getItem().getType() == Material.STRUCTURE_VOID)
                event.setCancelled(true);


    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(InventoryPickupItemEvent event) {
        if (event.isCancelled()) return;

        if (event.getItem().getItemStack() != null)
            if (event.getItem().getItemStack().getType() == Material.STRUCTURE_VOID)
                event.setCancelled(true);


    }

    @EventHandler
    public void onWitchThrow(ProjectileHitEvent event) {
        if (plugin.getDay() < 40) return;

        if (event.getEntity().getShooter() instanceof Witch) {
            if (event.getEntity() instanceof ThrownPotion potion) {
                int prob = new Random().nextInt(3) + 1;

                if (prob == 1) {
                    ItemStack item = new ItemStack(Material.SPLASH_POTION);
                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                    if (!meta.getCustomEffects().isEmpty() || meta.getCustomEffects().size() >= 1)
                        for (PotionEffect effect : meta.getCustomEffects())
                            meta.removeCustomEffect(effect.getType());

                    meta.addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 3), true);
                    item.setItemMeta(meta);
                    potion.setItem(item);
                } else if (prob == 2) {

                    int min = 60 * 5;

                    ItemStack item = new ItemStack(Material.SPLASH_POTION);
                    PotionMeta meta = (PotionMeta) item.getItemMeta();

                    if (!meta.getCustomEffects().isEmpty() || meta.getCustomEffects().size() >= 1) {
                        for (PotionEffect effect : meta.getCustomEffects()) {
                            meta.removeCustomEffect(effect.getType());
                        }
                    }

                    meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, min * 20, 2), true);
                    item.setItemMeta(meta);
                    potion.setItem(item);
                } else {

                    ItemStack s = new ItemStack(Material.SPLASH_POTION);
                    PotionMeta meta = (PotionMeta) s.getItemMeta();

                    if (!meta.getCustomEffects().isEmpty() || meta.getCustomEffects().size() >= 1) {
                        for (PotionEffect effect : meta.getCustomEffects()) {
                            meta.removeCustomEffect(effect.getType());
                        }
                    }

                    meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 20, 4), true);
                    s.setItemMeta(meta);
                    potion.setItem(s);
                }
            }

        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getNewItems().isEmpty()) {
            for (int i : event.getNewItems().keySet()) {
                ItemStack s = event.getNewItems().get(i);

                if (s != null) {

                    if (s.getType() == Material.STRUCTURE_VOID) {

                        event.getInventory().removeItem(s);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onIntWithEndRelic(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand() != null) {
            if (isRelic(event.getPlayer().getInventory().getItemInMainHand())) {
                event.setCancelled(true);
            }
        }

        if (event.getPlayer().getInventory().getItemInOffHand() != null) {
            if (isRelic(event.getPlayer().getInventory().getItemInOffHand())) {
                event.setCancelled(true);
            }
        }
    }

    public boolean isRelic(ItemStack stack) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;

        if (stack.getType() == Material.LIGHT_BLUE_DYE && stack.getItemMeta().getCustomModelDataComponent().getStrings().getFirst().equals("end_relic")) {
            return true;
        }
        return false;
    }
}
