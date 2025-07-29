package dev.yeruza.plugin.permadeath.plugin.listener.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.data.EndManager;
import dev.yeruza.plugin.permadeath.plugin.item.tool.ToolKit;
import dev.yeruza.plugin.permadeath.plugin.item.tool.ToolKits;
import dev.yeruza.plugin.permadeath.utils.TextFormat;

import java.util.List;

public class BlockListener implements Listener {

    public static List<ItemStack> NO_DAMAGE_TOOLS = select(ToolKits.ALMORITY, ToolKits.INFERNAL_NETHERITE, ToolKits.PURE_NETHERITE);

    private static List<ItemStack> select(ToolKit ...collections) {
        int count = collections.length;
        int index = count > 0 ? count - 1 : 0;


        return List.of(

        );
    }

    private final Permadeath plugin;

    public BlockListener() {
        this.plugin = Permadeath.getPlugin();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent event) {
        if (plugin.getEnd() != null && plugin.getDay() >= 30) {
            EndManager data = plugin.getEndData();

            if (data.getConfig().contains("end-options.regen-zone-location")) {
                Location loc = TextFormat.parseCoords(data.getConfig().getString("end-options.regen-zone-location"));

                if (event.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                    if (event.getBlock().getLocation().distance(loc) <= 10)
                        event.setCancelled(true);

            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockExplode(EntityExplodeEvent event) {
        if (plugin.getEndData() != null) {
            EndManager data = plugin.getEndData();

            if (data.getConfig().contains("end-options.regen-zone-location")) {
                Location loc = TextFormat.parseCoords(data.getConfig().getString("end-options.regen-zone-location"));

                for (Block block : event.blockList())
                    if (block.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                        if (block.getLocation().distance(loc) <= 10)
                            event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockCombust(BlockIgniteEvent event) {
        if (plugin.getEndData() != null) {
            EndManager data = plugin.getEndData();

            if (data.getConfig().contains("regen-zone-location")) {
                Location loc = TextFormat.parseCoords(data.getConfig().getString("regen-zone-location"));

                if (event.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                    if (event.getBlock().getLocation().distance(loc) >= 3)
                        event.setCancelled(true);

            }

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getEndData() != null && plugin.getDay() >= 30) {
            EndManager data = plugin.getEndData();

            if (data.getConfig().contains("end-options.regen-zone-location")) {
                Location loc = TextFormat.parseCoords(data.getConfig().getString("end-options.regen-zone-location"));

                if (event.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                    if (event.getBlock().getLocation().distance(loc) <= 3) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(TextFormat.write("&cNo puedes colocar bloques cerca de la Zona de Regeneración."));
                    }
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (plugin.getEnd() != null && plugin.getDay() >= 30) {
            EndManager data = plugin.getEndData();

            if (data.getConfig().contains("end-options.regen-zone-location")) {
                Location loc = TextFormat.parseCoords(data.getConfig().getString("end-options.regen-zone-location"));

                if (event.getBlock().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName()))
                    if (event.getBlock().getLocation().distance(loc) <= 10) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage("&cNo puedes romper bloques cerca de la Zona de Regeneración.");
                    }
            }
        }

        if (plugin.getDay() >= 50) {
            boolean damageEnable = true;
            ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
            for (ItemStack custom : NO_DAMAGE_TOOLS) {

                if (hand.getType() == custom.getType()) {


                    damageEnable = false;
                    break;
                }
            }

            if (damageEnable)
                if (plugin.getDay() <= 60)
                    event.getPlayer().damage(1.0D);
                else
                    event.getPlayer().damage(16.0D);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (plugin.getDay() >= 50) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFurnace(FurnaceSmeltEvent event) {
        if (plugin.getDay() >= 50) {
            if (event.getResult() != null) {
                if (event.getResult().getType() == Material.IRON_INGOT) {
                    ItemStack metal = event.getResult();
                    metal.setType(Material.IRON_NUGGET);
                    event.setResult(metal);
                }

                if (event.getResult().getType() == Material.GOLD_INGOT) {
                    ItemStack metal = event.getResult();
                    metal.setType(Material.GOLD_NUGGET);
                    event.setResult(metal);

                }
            }
        }

    }
}
