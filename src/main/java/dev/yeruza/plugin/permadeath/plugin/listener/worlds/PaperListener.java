package dev.yeruza.plugin.permadeath.plugin.listener.worlds;

import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;
import com.destroystokyo.paper.event.entity.EntityTeleportEndGatewayEvent;
import com.destroystokyo.paper.event.player.PlayerTeleportEndGatewayEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.EndGateway;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import dev.yeruza.plugin.permadeath.Permadeath;
import dev.yeruza.plugin.permadeath.plugin.listener.PluginAdaptedListener;
import dev.yeruza.plugin.permadeath.utils.TextFormat;
import dev.yeruza.plugin.permadeath.worlds.end.DemonPhase;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class PaperListener extends PluginAdaptedListener implements Listener {
    private final SplittableRandom random = new SplittableRandom();

    public PaperListener(Permadeath plugin) {
        super(plugin);
    }

    @EventHandler
    public void onProjectileHit(EnderDragonFireballHitEvent event) {
        AreaEffectCloud effect = event.getAreaEffectCloud();

        if (plugin.getEndTask() != null) {
            final List<Block> change = new ArrayList<>();

            Block block = plugin.getEnd().getHighestBlockAt(effect.getLocation());
            Location highest = plugin.getEnd().getHighestBlockAt(effect.getLocation()).getLocation();

            int structure = random.nextInt();

            switch (structure) {
                case 0 -> {
                    change.add(block.getRelative(BlockFace.NORTH));
                    change.add(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST));
                    change.add(block.getRelative(BlockFace.SOUTH));
                    change.add(block.getRelative(BlockFace.SOUTH_EAST));
                    change.add(block.getRelative(BlockFace.SOUTH_WEST));
                    change.add(block.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.SOUTH));
                    change.add(block.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.NORTH));
                    change.add(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH));
                }
                case 1 -> {
                    change.add(block.getRelative(BlockFace.NORTH));
                    change.add(block.getRelative(BlockFace.NORTH_EAST));
                    change.add(block);
                }
                case 2 -> {
                    change.add(block.getRelative(BlockFace.SOUTH));
                    change.add(block.getRelative(BlockFace.SOUTH_WEST));
                    change.add(block);
                }
                case 3 -> {
                    change.add(block.getRelative(BlockFace.NORTH));
                    change.add(block.getRelative(BlockFace.NORTH_EAST));
                    change.add(block);
                    change.add(block.getRelative(BlockFace.SOUTH));
                    change.add(block.getRelative(BlockFace.EAST));
                }
                case 4 -> {
                    change.add(block.getRelative(BlockFace.SOUTH));
                    change.add(block.getRelative(BlockFace.NORTH_WEST));
                    change.add(block);
                    change.add(block.getRelative(BlockFace.NORTH));
                    change.add(block.getRelative(BlockFace.WEST));
                }
            }

            if (plugin.getEndTask().getCurrentDemonPhase() == DemonPhase.NORMAL) {
                if (highest.getY() > 0) {
                    for (Block b : change) {
                        Location used = plugin.getEnd().getHighestBlockAt(new Location(plugin.getEnd(), b.getX(), b.getY(), b.getZ())).getLocation();
                        Block now = plugin.getEnd().getBlockAt(used);

                        if (now.getType() != Material.AIR)
                            now.setType(Material.BEDROCK);
                    }
                }
            } else {
                if (highest.getY() > 0) {
                    for (Block all : change) {
                        Location used = plugin.getEnd().getHighestBlockAt(new Location(plugin.getEnd(), all.getX(), all.getY(), all.getZ())).getLocation();
                        Block now = plugin.getEnd().getBlockAt(used);

                        if (now.getType() != Material.AIR)
                            now.setType(Material.BEDROCK);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onGatewayPortal(EntityTeleportEndGatewayEvent event) {
        if (plugin.getDay() < 40) return;

        if (plugin.getDay() >= 50) {
            if (plugin.getBeginning().isClosed()) {
                event.setCancelled(true);
                return;
            }
        }

        Entity entity = event.getEntity();
        Location coords = event.getFrom();
        World world = coords.getWorld();

        if (entity instanceof Player)
            return;
        event.setCancelled(true);

        Vector direction = entity.getLocation().getDirection();
        Vector velocity = entity.getVelocity();

        float pitch = entity.getLocation().getPitch();
        float yaw = entity.getLocation().getYaw();

        if (world.getPersistentDataContainer().has(plugin.getOverWorld().getKey())) {

            Location loc = plugin.getBeginningData().getBeginningPortal();
            loc.setDirection(direction);
            loc.setPitch(pitch);
            loc.setYaw(yaw);
            entity.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            entity.setVelocity(velocity);
        }

        if (world.getPersistentDataContainer().has(plugin.getBeginning().getKey())) {

            Bukkit.getScheduler().runTaskLater(plugin, () -> {

                Location loc = plugin.getOverWorld().getSpawnLocation();
                loc.setDirection(direction);
                loc.setPitch(pitch);
                loc.setYaw(yaw);
                entity.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                entity.setVelocity(velocity);

            }, 1L);
        }
    }

    @EventHandler
    public void onGatewayPortal(PlayerTeleportEndGatewayEvent event) {
        if (plugin.getDay() < 40) return;

        if (plugin.getDay() < 50) {
            if (event.getPlayer().getWorld().getPersistentDataContainer().has(plugin.getOverWorld().getKey()) || event.getPlayer().getWorld().getPersistentDataContainer().has(plugin.getBeginning().getKey())) {
                event.getPlayer().setNoDamageTicks(event.getPlayer().getMaximumNoDamageTicks());
                event.getPlayer().damage(event.getPlayer().getHealth() + 1.0D, (Entity) null);
                event.getPlayer().setNoDamageTicks(0);
                Bukkit.broadcast(TextFormat.write("&c&lEl jugador &4&l" + event.getPlayer().getName() + " &c&lentró a TheBeginning antes de tiempo."));
            }
        }

        if (plugin.getDay() >= 50) {
            if (plugin.getBeginning().isClosed()) {
                event.setCancelled(true);
                return;
            }

            EndGateway gateway = event.getGateway();
            Player player = event.getPlayer();
            Location coords = event.getFrom();
            World world = coords.getWorld();

            gateway.setExitLocation(gateway.getLocation());
            gateway.update();
            event.setCancelled(true);

            Vector direction = player.getLocation().getDirection();
            Vector velocity = player.getVelocity();

            float pitch = player.getLocation().getPitch();
            float yaw = player.getLocation().getYaw();

            if (world.getPersistentDataContainer().has(plugin.getOverWorld().getKey())) {

                Location loc = plugin.getBeginningData().getBeginningPortal();
                loc.setDirection(direction);
                loc.setPitch(pitch);
                loc.setYaw(yaw);
                player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.setVelocity(velocity);
            }

            if (world.getPersistentDataContainer().has(plugin.getBeginning().getKey())) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    
                    Location loc = plugin.getOverWorld().getSpawnLocation();
                    loc.setDirection(direction);
                    loc.setPitch(pitch);
                    loc.setYaw(yaw);

                    player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    player.setVelocity(velocity);

                }, 1L);
            }
        }
    }
}
